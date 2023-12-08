#!/usr/bin/env python
# coding: utf-8

# In[38]:
import numpy as np
import pandas as pd
from com.example.nstunews import Constrains
# In[39]:
directory=Constrains.uid
print(directory)
file_path = f'/data/data/com.example.nstunews/cache/{directory}/userData.csv'
print(file_path)
# In[40]:

#add header to csv
with open(file_path, 'r') as file:
    content = file.read()

# Line to add at the top
line_to_add = '"Post ID",Likes,Comments,Views\n'

# Combine the new line with the existing content
new_content = line_to_add + content

# Write the modified content back to the same file
with open(file_path, 'w') as file:
    file.write(new_content)

print(new_content)
#Load data from user interactions dataset
interactions_df = pd.read_csv(file_path)
interactions_df.head(10)
#Assign strength of each post by their reactions
event_type_strength = {
    'Views': 1.0,
    'Likes': 2.0,
    'Comments': 4.0,
}

interactions_df['eventStrength'] = interactions_df['Views']*event_type_strength['Views'] + interactions_df['Likes']*event_type_strength['Likes'] + interactions_df['Comments']*event_type_strength['Comments']

interactions_df.tail()


# In[42]:


#Data shape

interactions_df.shape


# In[43]:


interactions_df.insert(0,'ID',range(0,interactions_df.shape[0]))
interactions_df.head()


# In[44]:


#Count missing data
interactions_df.isna().sum()


# In[45]:


interactions_full_df = interactions_df[['ID', 'Post ID', 'eventStrength']]
print(interactions_full_df)

top_three_weight = interactions_full_df.nlargest(3, 'eventStrength')

top_three_post_ids = top_three_weight['Post ID'].tolist()
top_three_strengths = top_three_weight['eventStrength'].tolist()


def output_id():
    return top_three_post_ids

def output_str():
    return top_three_strengths

# In[47]:


#Computes the most popular items
#
# item_popularity_df = interactions_full_df.groupby('Post ID')['eventStrength'].sum().sort_values(ascending=False).reset_index()
# item_popularity_df.head(5)
# item_popularity_df.to_csv('/data/data/com.example.nstunews/cache/weighted_data.csv', index=False)
#
# #remove data
# with open(file_path, 'w') as file:
#     pass
