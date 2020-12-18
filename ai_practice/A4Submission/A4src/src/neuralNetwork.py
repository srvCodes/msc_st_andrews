import pickle
import math
import numpy as np
from sklearn.metrics import precision_recall_fscore_support


class FeedForwardNetwork():
    """
    Class to define behavious of the neural network.
    """

    def __init__(self, X, y=None, x_val=None, y_val=None, load_from_file=False, advanced=False):
        """
        Constructor.
        @param X: is the list of train features.
        @param y: is the list of train labels.
        @param x_val: is the list of validation features
        @param y_val: is the list of validation labels
        @param load_from_file: true while inferencing, i.e. load the saved configurations
        @param advanced: true in case of second advanced agent.
        """
        self.train_input = X
        self.epochs = 2000
        self.hidden_layer_size = 512
        self.learning_rate = 0.5
        self.momentum = 0.8
        self.batch_size = 32
        if load_from_file:
            # input and output dimensions are also loaded just to ensure avoiding future shape mismatches
            # also, this addresses the basic requirement of assignment for i/p and o/p units to be fixed
            filename = 'network_config_3.pkl' if advanced else 'network_config.pkl'
            network_params = pickle.load(open(filename, 'rb'))
            self.w1, self.w2, self.b1, self.b2, self.input_dim, self.output_dim = network_params
        else:
            self.train_output = y
            self.input_dim = X.shape[1]
            self.output_dim = y.shape[1]
            self.w1, self.b1, self.w2, self.b2 = self.initialize_weights_and_biases()
            self.validation_input = x_val
            self.validation_output = y_val

        self.prev_grad_w1 = np.zeros(self.w1.shape)
        self.prev_grad_w2 = np.zeros(self.w2.shape)
        self.prev_grad_b1 = np.zeros(self.b1.shape)
        self.prev_grad_b2 = np.zeros(self.b2.shape)

        self.input = None
        self.output = None
        self.early_stopping_patience = 5 if not advanced else 25
        self.advanced_level = advanced

    def initialize_weights_and_biases(self):
        """
        Function to randomly initialize weight and bias arrays.
        @return: the initialized arrays of weights and biases of the two layers.
        """
        w1 = np.random.randn(self.input_dim, self.hidden_layer_size)
        b1 = np.zeros((1, self.hidden_layer_size))

        w2 = np.random.randn(self.hidden_layer_size, self.output_dim)
        b2 = np.zeros((1, self.output_dim))

        return w1, b1, w2, b2

    @staticmethod
    def sigmoid_func(x):
        return 1 / (1 + np.exp(-x))

    @staticmethod
    def softmax(x):
        exponentials = np.exp(x - np.max(x, axis=1, keepdims=True))
        return exponentials / np.sum(exponentials, axis=1, keepdims=True)

    def forward_pass(self):
        """
        Function to perform a forward pass over the network.
        """
        z1 = np.dot(self.input, self.w1) + self.b1
        self.hidden_layer_output = self.sigmoid_func(z1)

        z2 = np.dot(self.hidden_layer_output, self.w2) + self.b2
        self.final_output = self.softmax(z2)

    @staticmethod
    def sigmoid_derivative_func(x):
        return x * (1 - x)

    @staticmethod
    def cross_entropy_loss(predicted, actual):
        n_samples = actual.shape[0]
        difference = predicted - actual
        return difference / n_samples

    @staticmethod
    def max_likelihood_error(predicted, actual):
        n_samples = actual.shape[0]
        log_prob = - np.log(predicted[np.arange(n_samples), actual.argmax(axis=1)])
        loss = np.sum(log_prob) / n_samples
        return loss

    def backpropagation(self):
        """
        Method to perform gradient descent for updating weights and biases using momentum.
        """
        loss = round(self.max_likelihood_error(self.final_output, self.output), 5)
        print(f'Train error is: {loss}', end=' ')

        final_output_delta = self.cross_entropy_loss(self.final_output, self.output)
        z2_delta = np.dot(final_output_delta, self.w2.T)
        hidden_output_delta = z2_delta * self.sigmoid_derivative_func(self.hidden_layer_output)

        ## perform weight and bias updates directly (without momentum)
        # self.w2 -= self.learning_rate * np.dot(self.hidden_layer_output.T, final_output_delta)
        # self.b2 -= self.learning_rate * np.sum(final_output_delta, axis=0, keepdims=True)
        # self.w1 -= self.learning_rate * np.dot(self.input.T, hidden_output_delta)
        # self.b1 -= self.learning_rate * np.sum(hidden_output_delta, axis=0)

        w2_update = self.learning_rate * np.dot(self.hidden_layer_output.T, final_output_delta)
        b2_update = self.learning_rate * np.sum(final_output_delta, axis=0, keepdims=True)
        w1_update = self.learning_rate * np.dot(self.input.T, hidden_output_delta)
        b1_update = self.learning_rate * np.sum(hidden_output_delta, axis=0)

        self.w2 += -(w2_update + (self.momentum * self.prev_grad_w2))
        self.w1 += -(w1_update + (self.momentum * self.prev_grad_w1))
        self.b2 += -(b2_update + (self.momentum * self.prev_grad_b2))
        self.b1 += -(b1_update + (self.momentum * self.prev_grad_b1))

        self.prev_grad_w1, self.prev_grad_w2 = w1_update, w2_update
        self.prev_grad_b1, self.prev_grad_b2 = b1_update, b2_update

    def save_config_to_file(self):
        """
        Function to save necessary parameters of trained model so as to be loaded later.
        """
        all_params = [self.w1, self.w2, self.b1, self.b2, self.input_dim, self.output_dim]
        filename = 'network_config_3.pkl' if self.advanced_level else 'network_config.pkl'
        print(f"Saving network configurations to '{filename}'!")
        pickle.dump(all_params, open(filename, 'wb'))

    def train(self):
        """
        Function to train the weights and biases of the network using backpropagation.
        """
        min_val_error = math.inf
        min_val_error_epoch = 0
        for epoch in range(self.epochs):
            self.input = self.train_input
            self.output = self.train_output
            print(f'Epoch: {epoch}', end=' ============== ')
            self.forward_pass()
            self.backpropagation()
            val_error = self.test()
            if val_error < min_val_error:
                print("Validation error decreased.", end=' ')
                min_val_error = val_error
                min_val_error_epoch = epoch
                # save to file the config with least validation error till now
                self.save_config_to_file()
            # stop training if min validation error has reached below 0.1 and
            # validation error hasn't decreased further after certain no. of epochs
            if epoch > min_val_error_epoch + self.early_stopping_patience:
                print(
                    f"Early stopping since validation error did not improve till {self.early_stopping_patience} epochs.")
                break

    def test(self, X_test=None, y_test=None):
        """
        Function to perform inference using the trained model.
        @param X_test: is supplied when testing using the csv file's data.
        @param y_test: is supplied when testing using the labels of the csv file.
        @return: loss value rounded off to 4 digits.
        """
        if X_test is None:
            self.input = self.validation_input
            self.output = self.validation_output
        else:
            self.input = X_test
            self.output = y_test
        self.forward_pass()
        loss = np.mean(self.max_likelihood_error(self.final_output, self.output))
        print(f'Validation error: {loss}')
        if X_test is not None:
            print(
                f"Precision-recall scores: {precision_recall_fscore_support(y_pred=np.argmax(self.final_output, axis=1), y_true=np.argmax(self.output, axis=1))}")
        return round(loss, 4)

    def predict(self, X_test):
        """
        Function to perform inference using just the test features (w/o labels).
        @param X_test: is the list of features fed by user through UI.
        @return: the output predicted by the network.
        """
        self.input = X_test
        self.forward_pass()
        return self.final_output

    @staticmethod
    def iterate_minibatches(inputs, targets, batch_size, shuffle=False):
        """
        Generator method to iterate over inputs using specified batch size.
        Code adapted from https://stackoverflow.com/questions/38157972/how-to-implement-mini-batch-gradient-descent-in-python
        @param inputs: is the input read from the data set.
        @param targets: are the labels.
        @param batch_size: is the batch size to divide into.
        @param shuffle: says whether to randomly shuffle the dataset before generating batches.
        @return:
        """
        assert inputs.shape[0] == targets.shape[0]
        if shuffle:
            indices = np.arange(inputs.shape[0])
            np.random.shuffle(indices)
        for start_idx in range(0, inputs.shape[0], batch_size):
            end_idx = min(start_idx + batch_size, inputs.shape[0])
            if shuffle:
                excerpt = indices[start_idx:end_idx]
            else:
                excerpt = slice(start_idx, end_idx)
            yield inputs[excerpt], targets[excerpt]

    def train_minibatch(self):
        """
        Method to train the neural network using minibatch gradient descent.
        """
        min_val_error = math.inf
        min_val_error_epoch = 0
        for epoch in range(self.epochs):
            print(f'Epoch: {epoch}', end=' ============== ')
            for idx, batch in enumerate(
                    self.iterate_minibatches(self.train_input, self.train_output, self.batch_size, shuffle=True)):
                print(f"| Batch: {idx}", end=' ')
                self.input, self.output = batch
                self.forward_pass()
                self.backpropagation()
            val_error = self.test()
            if val_error < min_val_error:
                print("Validation error decreased.", end=' ')
                min_val_error = val_error
                min_val_error_epoch = epoch
                # save to file the config with least validation error till now
                self.save_config_to_file()
            # stop training if min validation error is below 0.1 and
            # validation error hasn't decreased further after certain no. of epochs
            if min_val_error < 0.8 and (epoch > min_val_error_epoch + self.early_stopping_patience):
                print(
                    f"Early stopping since validation error did not improve till {self.early_stopping_patience} epochs.")
                break
