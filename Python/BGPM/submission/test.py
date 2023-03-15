#!/usr/bin/env python3
import json

file1 = open("out4.json", "r")
file2 = open("task_4 copy.json", "r")

data1 = json.load(file1)
data2 = json.load(file2)
print("start")
print(len(data1))
print(len(data2))

for k in data2:
    print(k)
    if k in data1:
        # print("check")
        for k2 in data2[k]:
            if k2 in data1[k]:
                if data1[k][k2] != data2[k][k2]:
                    print(k+k2)
                # else:
                #     print("match")
    else:
        print("no")

# 192.65.185.3
# 192.65.185.3103.90.242.0/23
# 192.65.185.3117.202.128.0/20
# 192.65.185.3117.216.112.0/20
# 192.65.185.363.92.224.0/19
# 192.65.185.363.92.224.0/20
# 192.65.185.386.106.192.0/21
