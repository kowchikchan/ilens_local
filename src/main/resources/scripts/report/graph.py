from matplotlib import pyplot as plt
import os, json
try:
    f = open(os.path.abspath(os.path.join(os.path.dirname(__file__), os.pardir)) + "/userList.json", "r")
    data = json.loads(f.read())
except IOError as e:
    raise IOError("Users list not found", e)

x = range(1, 30)
default_x_ticks = range(len(x))
plt.xticks(default_x_ticks, x)

plt.plot(data['onTimeList'], linestyle='-', marker='o', color='b', label='On Time Entry')
plt.plot(data['graceTimeList'], linestyle='-', marker='o', color='g', label='Grace Time Entry')
plt.plot(data['lateTimeList'], linestyle='-', marker='o', color='r', label='Beyond Grace Entry')
plt.legend()
plt.savefig(os.path.dirname(__file__) + '/reportGraph.png')
