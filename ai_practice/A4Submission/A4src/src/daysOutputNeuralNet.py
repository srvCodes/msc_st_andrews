import math
import pickle

import numpy as np
from sklearn.metrics import explained_variance_score, mean_squared_error

class FeedForwardNetwork():
    def __init__(self, X, y=None, X_val=None, y_val=None, load_from_file=False):
        self.train_input = X
        self.epochs = 10000
        self.hidden_layer_size = 128
        self.learning_rate = 0.5
        self.momentum = 0.8
        if load_from_file:
            # input and output dimensions are also loaded just to ensure avoiding future shape mismatches
            # also, this addresses the basic requirement of assignment for i/p and o/p units to be fixed
            network_params = pickle.load(open('network_config_2.pkl', 'rb'))
            self.w1, self.w2, self.b1, self.b2, self.input_dim, self.output_dim = network_params
        else:
            self.train_output = y
            self.input_dim = X.shape[1]
            self.output_dim = 1
            self.w1, self.b1, self.w2, self.b2 = self.initialize_weights_and_biases()
            self.validation_input = X_val
            self.validation_output = y_val

        self.prev_grad_w1 = np.zeros(self.w1.shape)
        self.prev_grad_w2 = np.zeros(self.w2.shape)
        self.prev_grad_b1 = np.zeros(self.b1.shape)
        self.prev_grad_b2 = np.zeros(self.b2.shape)

        self.input = None
        self.output = None
        self.early_stopping_patience = 500

    def initialize_weights_and_biases(self):
        w1 = np.random.randn(self.input_dim, self.hidden_layer_size)
        b1 = np.zeros((1, self.hidden_layer_size))

        w2 = np.random.randn(self.hidden_layer_size, self.output_dim)
        b2 = np.zeros((1, self.output_dim))

        return w1, b1, w2, b2

    @staticmethod
    def sigmoid_func(x):
        return 1 / (1 + np.exp(-x))

    @staticmethod
    def relu(x):
        return np.maximum(0, x)

    def feedforward_layer(self):
        z1 = np.dot(self.input, self.w1) + self.b1
        self.hidden_layer_output = self.sigmoid_func(z1)

        z2 = np.dot(self.hidden_layer_output, self.w2) + self.b2
        self.final_output = self.relu(z2)

    @staticmethod
    def sigmoid_derivative_func(x):
        return x * (1-x)

    @staticmethod
    def cross_entropy_loss(predicted, actual):
        n_samples = actual.shape[0]
        difference = predicted - actual
        return difference/n_samples

    @staticmethod
    def mean_squared_error(predicted, actual):
        squared_error = 1/2 * np.mean((predicted-actual)**2)
        return squared_error

    def backpropagation(self):
        loss = self.mean_squared_error(self.final_output, self.output)
        print(f'Train error: {loss}', end=' ')

        final_output_delta = self.cross_entropy_loss(self.final_output, self.output)
        z2_delta = np.dot(final_output_delta, self.w2.T)
        hidden_output_delta = z2_delta * self.sigmoid_derivative_func(self.hidden_layer_output)

        '''Without using momentum, weights updated directly:'''
        # self.w2 -= self.learning_rate * np.dot(self.hidden_layer_output.T, final_output_delta)
        # self.b2 -= self.learning_rate * np.sum(final_output_delta, axis=0, keepdims=True)
        # self.w1 -= self.learning_rate * np.dot(self.input.T, hidden_output_delta)
        # self.b1 -= self.learning_rate * np.sum(hidden_output_delta, axis=0)

        '''Using momentum, weights updated by adding fractions from previous time step: '''
        w2_update = self.learning_rate * np.dot(self.hidden_layer_output.T, final_output_delta)
        b2_update = self.learning_rate * np.sum(final_output_delta, axis=0, keepdims=True)
        w1_update = self.learning_rate * np.dot(self.input.T, hidden_output_delta)
        b1_update = self.learning_rate * np.sum(hidden_output_delta, axis=0)

        self.w2 += -(w2_update + (self.momentum*self.prev_grad_w2))
        self.w1 += -(w1_update + (self.momentum*self.prev_grad_w1))
        self.b2 += -(b2_update + (self.momentum*self.prev_grad_b2))
        self.b1 += -(b1_update + (self.momentum*self.prev_grad_b1))

        self.prev_grad_w1, self.prev_grad_w2 = w1_update, w2_update
        self.prev_grad_b1, self.prev_grad_b2 = b1_update, b2_update

    def save_config_to_file(self):
        all_params = [self.w1, self.w2, self.b1, self.b2, self.input_dim, self.output_dim]
        pickle.dump(all_params, open('network_config_2.pkl', 'wb'))

    # def train(self):
    #     for epoch in range(self.epochs):
    #         print(f'Epoch: {epoch}')
    #         self.feedforward_layer()
    #         self.backpropagation()
    #     self.save_config_to_file()

    def train(self):
        min_val_error = math.inf
        min_val_error_epoch = 0
        for epoch in range(self.epochs):
            self.input = self.train_input
            self.output = self.train_output
            print(f'Epoch: {epoch}', end=' ============== ')
            self.feedforward_layer()
            self.backpropagation()
            val_error = np.round(self.test(), 7)
            if val_error < min_val_error:
                min_val_error = val_error
                min_val_error_epoch = epoch
                # save to file the config with least validation error till now
                self.save_config_to_file()
            # stop training if validation error hasn't decreased further after certain no. of epochs
            if epoch > min_val_error_epoch + self.early_stopping_patience:
                print(f"Early stopping since validation error did not improve till {self.early_stopping_patience} epochs.")
                break

    def test(self, X_test=None, y_test=None):
        if X_test is None:
            self.input = self.validation_input
            self.output = self.validation_output
        else:
            self.input = X_test
            self.output = y_test
        self.feedforward_layer()
        loss = self.mean_squared_error(self.final_output, self.output)
        print(f'Test Error: {loss}')
        if X_test is not None:
            print(f"Explained variance score: {explained_variance_score(y_true=self.output, y_pred=self.final_output)}")
        return loss

    def predict(self, X_test):
        self.input = X_test
        self.feedforward_layer()
        return self.final_output
