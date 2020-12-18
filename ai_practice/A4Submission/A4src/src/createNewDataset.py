import numpy as np

def returnNewDf(df):
    """
    Function to create a new dataframe containing the number of days.
    @param: df is the original dataframe with label-encoded features
    @rtype: the updated dataframe with last column replaced by number of days to resolve the query
    """
    df.drop('Response Team', axis=1, inplace=True)
    df['Days'] = df.apply(lambda x: 3 * ((x[np.isfinite(x)][:9]).sum()) + 2, axis=1)
    print(df.head(5))
    df.to_csv('train_data_for_days.csv', index=False)
    return df
