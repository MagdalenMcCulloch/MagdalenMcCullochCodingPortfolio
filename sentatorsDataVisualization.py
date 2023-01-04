#to run this file make sure you have pandas downloaded, and the two files "Senate_Data.csv" and "Race_Data.csv" saved in the same folder.
# I provided the files you need to run and an image of the output called "SenatorsDataVisualization.png"
import pandas as pd
import matplotlib.pyplot as plt

df = pd.read_csv("Senate_Data.csv")
rd = pd.read_csv("Race_Data.csv")


fig, ax = plt.subplots(ncols=2, figsize=(15, 5))

senators = df.groupby('race')

ax[0].pie(rd.pct, labels=rd.race)
ax[0].set_title('US Population by Race')

ax[1].pie(senators.population.sum(), labels= senators.groups.keys())
ax[1].set_title('% of US population represented by Senators in each party')

plt.show()
