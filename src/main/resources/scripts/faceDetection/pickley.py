import pickle

def clear_pickle_file(file_path):
    # Open the file in write-binary mode
    with open(file_path, 'wb') as file:
        # Write an empty dictionary to clear the contents
        pickle.dump({}, file)

# Paths to your pickle files
pickle_files = ['AdminFeatures.pickle', 'AdminLabels.pickle']

# Clear each pickle file
for file_path in pickle_files:
    clear_pickle_file(file_path)

print("Pickle files have been cleared.")
