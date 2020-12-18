from sklearn.preprocessing import LabelEncoder
from sklearn.preprocessing import LabelBinarizer, OneHotEncoder
import pickle
import numpy as np

class DataEncoder():
    """
    Class to handle encoding of csv data to labels and one-hot forms.
    """

    def label_encode_columns(self, df, load_from_file=False):
        """
        Class to encode classes in each column to integer labels.
        @param df: the original dataframe
        @param load_from_file: true while predicting, specifies that a saved encoder dictionary should be loaded.
        @return:
        """

        for index, column_name in enumerate(list(df.columns)):
            df[column_name] = self.encode_labels(df[column_name].tolist(), column_name, load_from_file)
            # df[column_name] = self.load_and_transform_encoder(df[column_name], column_name, load_from_file)
        return df

    def encode_labels(self, list_of_strings, column_name, load_from_file=False):
        """
        Helper function to encode labels to integers.
        @param list_of_strings: is the list of classes in a column of dataframe.
        @param column_name: is the column label, e.g. Students, etc.
        @param load_from_file: true if a saved encoding is to be loaded to carry out the transformation.
        @return: numpy array of strings mapped to corresponding integer classes.
        """
        data_classes = list(sorted(set(list_of_strings)))
        # print(f'Data classes: {data_classes}')
        if load_from_file:
            mapping_dictionary = pickle.load(open('labelEncoder_' + column_name.split(' ')[0] + '.pkl', 'rb'))
        else:
            mapping_dictionary = dict(zip(data_classes, range(0, len(data_classes))))
            pickle.dump(mapping_dictionary, open('labelEncoder_' + column_name.split(' ')[0] + '.pkl', 'wb'))
        # print("Mapping dict: ", mapping_dictionary)
        mapped_labels = [mapping_dictionary[i] for i in list_of_strings]
        # print(mapped_labels[:10])
        mapped_labels = np.asarray(mapped_labels)
        return mapped_labels

    # """
    #     Function for label encoding using scikit-learn's LabelEncoder
    # """
    # def load_and_transform_encoder(self, values, column_name, load_from_file=False):
    #     encoder = pickle.load(open('labelEncoder_' + column_name.split(' ')[0] + '.pkl', 'rb')) if load_from_file else \
    #         LabelEncoder()
    #     new_values = encoder.transform(values) if load_from_file else encoder.fit_transform(values)
    #     if not load_from_file:
    #         pickle.dump(encoder, open('labelEncoder_' + column_name.split(' ')[0] + '.pkl', 'wb'))
    #     return new_values

    @staticmethod
    def manual_one_hot_encoder(list_of_labels, new_entry=False):
        """
        Function to encode labels to one-hot form.
        @param list_of_labels: classes in integer format.
        @param new_entry: can be set to true if there are other classes than pre-defined 5 classes
        @return: array of one-hot encoded labels
        """
        if not new_entry:
            no_of_classes = np.amax(list_of_labels) + 1
        else:
            no_of_classes = 5  # +1 because python's range operator considers [start, end)
        one_hot_encoded_labels = np.zeros(shape=(len(list_of_labels), no_of_classes), dtype=int)
        for idx, each_array in enumerate(list_of_labels):
            all_zeros = np.array([0 for _ in range(no_of_classes)])
            np.put(all_zeros, each_array[0], 1)
            one_hot_encoded_labels[idx] = all_zeros
        return one_hot_encoded_labels

    # @staticmethod
    # def one_hot_encode(list_of_labels):
    #     print(list_of_labels)
    #     encoder = OneHotEncoder(sparse=False)
    #     onehot_encoded = encoder.fit_transform(list_of_labels)
    #     return onehot_encoded


class TextInterface():
    """
    Class to manage the text UI functionalities.
    """
    def __init__(self, train_data):
        """
        Constructor.
        @param train_data: is the dataframe after label and one-hot encoding.
        """
        print("Data received: ")
        print(train_data.head(5))
        self.train_data = train_data
        self.feature_names = ['Request', 'Incident', 'WebServices', 'Login', 'Wireless', 'Printing',
                              'IdCards', 'Staff', 'Students']
        self.features_for_prediction = self.get_user_inputs()

    def get_user_inputs(self):
        """
        Function to read user inputs for each feature.
        @return: list of complete input features.
        """
        print("Please enter 'Yes/yes/y' for yes and 'No/no/n' for no saying whether the specified tag applies for the ticket...")
        all_inputs = []
        label_dict = {'Yes': 'Yes', 'y': 'Yes', 'yes': 'Yes', 'No': 'No', 'n': 'No', 'no': 'No'}
        for (idx, name) in enumerate(self.feature_names):
            label = input(name + ": ")
            all_inputs.append(label_dict.get(label))
            print(f"Press 'y' if you are tired of giving inputs, press 'n' otherwise:")
            if input() == 'y':
                break

        # replace the rest of inputs with the maximum occurring value of that column
        remaining_inputs = len(self.feature_names) - len(all_inputs)
        if remaining_inputs > 0:
            for each in range(remaining_inputs):
                index = len(all_inputs)
                average_value = self.train_data[self.feature_names[index]].mean()
                answer = 'No' if average_value < 0.5 else 'Yes'
                all_inputs.append(answer)

        print("Inputs being used for prediction: ", all_inputs)
        return all_inputs

    def encode_user_data(self):
        """
        Function to encode data received from user through UI.
        @return: array of encoded features.
        """
        data_encoder = DataEncoder()
        features = [[each] for each in self.features_for_prediction]
        encoded_features = []
        for idx, column_name in enumerate(self.feature_names):
            encoded_features.append(data_encoder.encode_labels(features[idx],
                                                               column_name,
                                                               load_from_file=True))
        print("Encoded features: ", encoded_features)
        return np.array(encoded_features)
