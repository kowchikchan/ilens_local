import os, json, operator, numpy as np, matplotlib.pyplot as plt
import matplotlib.pyplot as plt1
import matplotlib.patches as mpatches

plt.rc('xtick', labelsize=23)
plt.rc('ytick', labelsize=20)

angle = 0
barWidth = 0.25
plt.figure(figsize=(38, 16))

try:
    f = open(os.path.abspath(os.path.join(os.path.dirname(__file__), os.pardir)) + "/userList.json", "r")
    data = json.loads(f.read())
except IOError as e:
    raise IOError("Users list not found", e)


def autoLabel(rects, percentage):
    for i in range(len(rects)):
        width = rects[i].get_width()
        plt.text(rects[i].get_width() + 2, rects[i].get_y() + 0.5 * rects[i].get_height(),
                 '%s' % str(int(width)) + "%",
                 ha='center', va='center', fontsize=22, fontweight='bold')

        plt.text(102, rects[i].get_y() + 0.5 * rects[i].get_height(),
                 '%s' % str(percentage[i]),
                 ha='center', va='center', fontsize=22, fontweight='bold')


def reportGraph():
    global angle
    # set height of bar
    onTime = data['onTimeList']
    graceTime = data['graceTimeList']
    beyondGraceTime = data['lateTimeList']
    dt = data['dateList']
    if len(onTime) > 2: angle = 16

    br1 = np.arange(len(onTime))
    br2 = [x + barWidth for x in br1]
    br3 = [x + barWidth for x in br2]

    onTimePlot = plt.bar(br1, onTime, color='#76b083', width=barWidth, edgecolor='#76b083', label='on time entry')
    graceTimePlot = plt.bar(br2, graceTime, color='#d0a71a', width=barWidth, edgecolor='#d0a71a',
                            label='grace time entry')
    beyondTimePlot = plt.bar(br3, beyondGraceTime, color='#c63535', width=barWidth, edgecolor="#c63535",
                             label='beyond grace time')
    # plt.xlabel('Date', fontweight='bold', fontsize=32)
    plt.ylabel('Entries', fontweight='bold', fontsize=22)
    plt.xticks([r + barWidth for r in range(len(onTime))], [x for x in dt], rotation=angle, ha='right')
    plt.yticks([f for f in range(1, len(onTime))])

    # legend.
    plt.legend()
    plt.legend(prop={'size': 15})

    # chart values.
    plt.bar_label(onTimePlot, padding=3, fontsize=22)
    plt.bar_label(graceTimePlot, padding=3, fontsize=22)
    plt.bar_label(beyondTimePlot, padding=3, fontsize=22)

    plt.savefig(os.path.dirname(__file__) + '/reportGraph.png')


def performanceGraph():
    plt1.rc('xtick', labelsize=16)
    plt1.rc('ytick', labelsize=20)

    plt1.figure(figsize=(38, 16))
    totalHours = len(data['lateTimeList']) * 8

    performanceData = data['performance']
    performanceData = dict(sorted(performanceData.items(), key=operator.itemgetter(1)))
    users = []
    hours = []
    percentage = []
    for val in performanceData:
        users.append(val)
        if performanceData.get(val) > 60:
            per = round(performanceData.get(val) / 60, 2)
        else:
            per = float(str(0) + "." + str(performanceData.get(val)))
        roundedPercentage = round((per / totalHours) * 100, 2)
        if roundedPercentage > 100:
            roundedPercentage = 100
        percentage.append(roundedPercentage)
        hours.append(round(per))

    profit_color = [{p < 50: '#d6423a', 50 <= p <= 90: '#ccb350', p > 90: '#1d470a'}[True] for p in percentage]

    rects = plt1.barh(users, percentage, color=profit_color)
    autoLabel(rects, hours)
    # plt1.ylabel('Users', fontweight='bold', fontsize=22)
    plt1.xlabel('User performance percentage', fontweight='bold', fontsize=22)
    # plt1.xticks([f for f in range(1, 101, 18)])
    plt1.xlim(0, 100, 20)

    # plt1.text(98, 5.7, "Spent time\n(Hrs.)", fontsize=22, fontweight='bold')

    poor = mpatches.Patch(color='#d6423a', label='0-50%')
    moderate = mpatches.Patch(color='#ccb350', label='51-80%')
    excellent = mpatches.Patch(color='#1d470a', label='81-100%')
    plt1.legend(handles=[poor, moderate, excellent], prop={'size': 20})

    plt1.savefig(os.path.dirname(__file__) + '/performanceGraph.png')


if __name__ == "__main__":
    reportGraph()
    performanceGraph()
