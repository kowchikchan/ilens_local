import numpy as np
import matplotlib.pyplot as plt
import os, json
plt.rc('xtick', labelsize=20)
plt.rc('ytick', labelsize=20)

barWidth = 0.25
plt.figure(figsize=(38, 13))

try:
    f = open(os.path.abspath(os.path.join(os.path.dirname(__file__), os.pardir)) + "/userList.json", "r")
    data = json.loads(f.read())
except IOError as e:
    raise IOError("Users list not found", e)

# set height of bar
onTime = data['onTimeList']
graceTime = data['graceTimeList']
beyondGraceTime = data['lateTimeList']
dt = data['dateList']

br1 = np.arange(len(onTime))
br2 = [x + barWidth for x in br1]
br3 = [x + barWidth for x in br2]

onTimePlot = plt.bar(br1, onTime, color='#76b083', width=barWidth, edgecolor='#76b083', label='on time entry')
graceTimePlot = plt.bar(br2, graceTime, color='#d0a71a', width=barWidth, edgecolor='#d0a71a', label='grace time entry')
beyondTimePlot = plt.bar(br3, beyondGraceTime, color='#c63535', width=barWidth, edgecolor="#c63535",
                         label='beyond grace time')

plt.xlabel('Date', fontweight='bold', fontsize=13)
plt.ylabel('Entries', fontweight='bold', fontsize=13)
plt.xticks([r + barWidth for r in range(len(onTime))], [x for x in dt])

plt.legend()

plt.bar_label(onTimePlot, padding=3, fontsize=12)
plt.bar_label(graceTimePlot, padding=3, fontsize=12)
plt.bar_label(beyondTimePlot, padding=3, fontsize=12)

plt.savefig(os.path.dirname(__file__) + '/reportGraph.png')
