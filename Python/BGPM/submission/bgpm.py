#!/usr/bin/env python3

from distutils.command.build_scripts import first_line_re
from email.errors import NonASCIILocalPartDefect
from queue import Empty
import pybgpstream
from collections import defaultdict
import json

"""
CS 6250 BGP Measurements Project

Notes:
- Edit this file according to the project description and the docstrings provided for each function
- Do not change the existing function names or arguments
- You may add additional functions but they need to be contained entirely in this file
"""


# Task 1A: Unique Advertised Prefixes Over Time
def unique_prefixes_by_snapshot(cache_files):
    """
    Retrieve the number of unique IP prefixes from each of the input BGP data files.

    Args:
        cache_files: A chronologically sorted list of absolute (also called "fully qualified") path names

    Returns:
        A list containing the number of unique IP prefixes for each input file.
        For example: [2, 5]
    """
    result = []
    for fpath in cache_files:
        # stream = pybgpstream.BGPStream(data_interface="singlefile")
        stream = pybgpstream.BGPStream(data_interface="singlefile", filter='ipv 4') # ED #662
        stream.set_data_interface_option("singlefile", "rib-file", fpath)

        prefixes = [] 
        # https://bgpstream.caida.org/bundles/caidabgpstreamwebhomepage/docs/tutorials/code/pybgpstream-moas.py
        for rec in stream.records():
            for elem in rec:
                pfx = elem.fields["prefix"]
                prefixes.append(pfx)
        result.append(len(set(prefixes))) # https://datagy.io/python-count-unique-values-list/
    # print(result)
    return result


# Task 1B: Unique Autonomous Systems Over Time
def unique_ases_by_snapshot(cache_files):
    """
    Retrieve the number of unique ASes from each of the input BGP data files.

    Args:
        cache_files: A chronologically sorted list of absolute (also called "fully qualified") path names

    Returns:
        A list containing the number of unique ASes for each input file.
        For example: [2, 5]
    """

    result = []
    for fpath in cache_files:
        # stream = pybgpstream.BGPStream(data_interface="singlefile")
        stream = pybgpstream.BGPStream(data_interface="singlefile", filter='ipv 4') # ED #662
        stream.set_data_interface_option("singlefile", "rib-file", fpath)

        ases = [] 
        # https://bgpstream.caida.org/bundles/caidabgpstreamwebhomepage/docs/tutorials/code/pybgpstream-moas.py
        for rec in stream.records():
            for elem in rec:
                if elem.fields['as-path'] == "": # has one more count than solution #739
                    continue 

                aspath = elem.fields["as-path"].split(" ")
                if len(aspath) > 0: 
                    ases.extend(aspath)
        result.append(len(set(ases)))

    # print(result)
    return result


# Task 1C: Top-10 Origin AS by Prefix Growth
def top_10_ases_by_prefix_growth(cache_files):
    """
    Compute the top 10 origin ASes ordered by percentage increase (smallest to largest) of advertised prefixes.

    Args:
        cache_files: A chronologically sorted list of absolute (also called "fully qualified") path names

    Returns:
        A list of the top 10 origin ASes ordered by percentage increase (smallest to largest) of advertised prefixes
        AS numbers are represented as strings.

        For example: ["777", "1", "6"]
          corresponds to AS "777" as having the smallest percentage increase (of the top ten) and AS "6" having the
          highest percentage increase (of the top ten).
      """
    growth = {}
    firsttime_as = {}
    lasttime_as = {}
    for fpath in cache_files:
        stream = pybgpstream.BGPStream(data_interface="singlefile", filter='ipv 4') # ED #662
        stream.set_data_interface_option("singlefile", "rib-file", fpath)
        
        # https://bgpstream.caida.org/bundles/caidabgpstreamwebhomepage/docs/tutorials/code/pybgpstream-moas.py
        prefix_count = defaultdict(set)
        for rec in stream.records():
            for elem in rec:
                if elem.fields['as-path'] == "": # has one more count than solution #739
                    continue 

                pfx = elem.fields["prefix"]
                aspath = elem.fields["as-path"].split(" ")
                if len(aspath) > 0:
                    origin_as = aspath[-1]
                    prefix_count[origin_as].add(pfx)

        for origin_as in prefix_count:
            if origin_as not in firsttime_as: firsttime_as[origin_as] = len(prefix_count[origin_as])
            else: lasttime_as[origin_as] = len(prefix_count[origin_as])

    for origin_as in firsttime_as:
        if origin_as in lasttime_as: growth[origin_as] = (lasttime_as[origin_as] - firsttime_as[origin_as]) / firsttime_as[origin_as] 

    temp = dict(sorted(growth.items(), key=lambda x:x[1])) # https://www.freecodecamp.org/news/sort-dictionary-by-value-in-python
    top10 = list(temp.keys())
    # print(top10[-10:]) 
    return top10[-10:]


# Task 2: Routing Table Growth: AS-Path Length Evolution Over Time
def shortest_path_by_origin_by_snapshot(cache_files):
    """
    Compute the shortest AS path length for every origin AS from input BGP data files.

    Retrieves the shortest AS path length for every origin AS for every input file.
    Your code should return a dictionary where every key is a string representing an AS name and every value is a list
    of the shortest path lengths for that AS.

    Note: If a given AS is not present in an input file, the corresponding entry for that AS and file should be zero (0)
    Every list value in the dictionary should have the same length.

    Args:
        cache_files: A chronologically sorted list of absolute (also called "fully qualified") path names

    Returns:
        A dictionary where every key is a string representing an AS name and every value is a list, containing one entry
        per file, of the shortest path lengths for that AS
        AS numbers are represented as strings.

        For example: {"455": [4, 2, 3], "533": [4, 1, 2]}
        corresponds to the AS "455" with the shortest path lengths 4, 2 and 3 and the AS "533" with the shortest path
        lengths 4, 1 and 2.
    """
    shortest_path = defaultdict(list)
    length = 0
    for fpath in cache_files:
        stream = pybgpstream.BGPStream(data_interface="singlefile", filter='ipv 4') # ED #662
        stream.set_data_interface_option("singlefile", "rib-file", fpath)
        
        length+=1
        # https://bgpstream.caida.org/bundles/caidabgpstreamwebhomepage/docs/tutorials/code/pybgpstream-moas.py
        ases_count = {}
        for rec in stream.records():
            for elem in rec:
                if elem.fields['as-path'] == "": # has one more count than solution #739
                    continue 

                aspath = elem.fields["as-path"].split(" ")
                origin_as = aspath[-1]
                # if len(set(aspath)) == 1:
                #     ases_count[origin_as] = 0
                
                if len(set(aspath)) > 1:
                    # origin_as = aspath[-1]
                    if origin_as not in ases_count:
                        ases_count[origin_as] = len(set(aspath))
                    elif len(set(aspath)) < ases_count.get(origin_as):
                        ases_count[origin_as] = len(set(aspath))
                # else:
                #     origin_as = aspath[-1]
                #     ases_count[origin_as] = 0
                #     shortest_path.setdefault(origin_as,[])
                    
        # print(ases_count.get("1")) # show none instead of 0 
        # for key, value in ases_count.items():
        #     if value is None:
        #         ases_count[key] = 0

        for origin_as in ases_count: # getting 0 
            while len(shortest_path[origin_as]) < length:
                shortest_path[origin_as].extend([0])
            # shortest_path[origin_as].extend([0])
            # shortest_path[origin_as][length-1] = 0
        # for origin_as in ases_count:
            # print(ases_count.get("1"))
            # if ases_count[origin_as] == 0: # is None:
            #     shortest_path[origin_as].extend([0])
            #     print("padding")
            # else: 
            # if origin_as not in shortest_path:
            #     while len(shortest_path[origin_as]) < length:
            #         shortest_path[origin_as].extend([0])
            shortest_path[origin_as].append(ases_count.get(origin_as))
            if len(shortest_path[origin_as]) > length: 
                shortest_path[origin_as].pop(length-1)

    for origin_as in shortest_path: # some entries lack last 0 in list
        while len(shortest_path[origin_as]) < length:
            shortest_path[origin_as].extend([0])

    # shortest_path = dict(ases_count)
    # print(shortest_path["204234"]) # 1000: 0606 
    # print(shortest_path)
    result = dict(shortest_path)
    # with open("out.json","w") as fp:
    #     json.dump(temp, fp)
    return result


# Task 3: Announcement-Withdrawal Event Durations
def aw_event_durations(cache_files):
    """
    Identify Announcement and Withdrawal events and compute the duration of all explicit AW events in the input BGP data

    Args:
        cache_files: A chronologically sorted list of absolute (also called "fully qualified") path names

    Returns:
        A dictionary where each key is a string representing the IPv4 address of a peer (peerIP) and each value is a
        dictionary with keys that are strings representing a prefix and values that are the list of explicit AW event
        durations (in seconds) for that peerIP and prefix pair.

        For example: {"127.0.0.1": {"12.13.14.0/24": [4.0, 1.0, 3.0]}}
        corresponds to the peerIP "127.0.0.1", the prefix "12.13.14.0/24" and event durations of 4.0, 1.0 and 3.0.
    """

    result = defaultdict(dict)
    announced = defaultdict(dict)
    withdraw = defaultdict(dict)
    for fpath in cache_files:
        stream = pybgpstream.BGPStream(data_interface="singlefile", filter='ipv 4') # ED #662
        stream.set_data_interface_option("singlefile", "upd-file", fpath)
        
        for rec in stream.records():
            for elem in rec:
                ip = elem.peer_address
                pfx = elem.fields["prefix"]
                time = elem.time

                if elem.type == "A":
                    announced[ip][pfx] = time

                if elem.type == "W":
                    withdraw[ip][pfx] = time
                    if ip in announced and pfx in announced[ip]:
                        if withdraw[ip][pfx] > announced[ip][pfx]:
                            if ip not in result: result.setdefault(ip, {})
                        
                            if pfx not in result[ip]: result[ip].setdefault(pfx, [])
                            
                            duration = withdraw[ip][pfx] - announced[ip][pfx]
                            result[ip][pfx].append(duration)
                        
                        announced[ip].pop(pfx)
                        withdraw[ip].pop(pfx)
    
    return dict(result)        


# Task 4: RTBH Event Durations
def rtbh_event_durations(cache_files):
    """
    Identify blackholing events and compute the duration of all RTBH events from the input BGP data

    Identify events where the IPv4 prefixes are tagged with at least one Remote Triggered Blackholing (RTBH) community.

    Args:
        cache_files: A chronologically sorted list of absolute (also called "fully qualified") path names

    Returns:
        A dictionary where each key is a string representing the IPv4 address of a peer (peerIP) and each value is a
        dictionary with keys that are strings representing a prefix and values that are the list of explicit RTBH event
        durations (in seconds) for that peerIP and prefix pair.

        For example: {"127.0.0.1": {"12.13.14.0/24": [4.0, 1.0, 3.0]}}
        corresponds to the peerIP "127.0.0.1", the prefix "12.13.14.0/24" and event durations of 4.0, 1.0 and 3.0.
    """

    # https://bgpstream.caida.org/bundles/caidabgpstreamwebhomepage/docs/tutorials/code/pybgpstream-communities.py
    result = defaultdict(dict)
    announced = defaultdict(dict)
    withdraw = defaultdict(dict)
    for fpath in cache_files:
        stream = pybgpstream.BGPStream(data_interface="singlefile", filter='ipv 4') # ED #662
        stream.set_data_interface_option("singlefile", "upd-file", fpath)
        
        for rec in stream.records():
            for elem in rec:
                ip = elem.peer_address
                pfx = elem.fields["prefix"]
                time = elem.time

                if elem.type == "A":
                    community = elem.fields["communities"]
                    if community == "":
                        continue
                            
                    string = str(community)
                    # print(string.find("666")) # RTBH: 666 from  ED#662
                    if string.find(":666\'") > -1: # some tage has 6666
                        # if ip == "192.65.185.140" and pfx == "64.192.0.0/24": print(string)
                        announced[ip][pfx] = time
                    elif pfx in announced[ip]:
                        announced[ip].pop(pfx)

                if elem.type == "W":
                    withdraw[ip][pfx] = time
                    if ip in announced and pfx in announced[ip]:
                        if withdraw[ip][pfx] > announced[ip][pfx]:
                            if ip not in result: result.setdefault(ip, {})
                        
                            if pfx not in result[ip]: result[ip].setdefault(pfx, [])
                            
                            duration = withdraw[ip][pfx] - announced[ip][pfx]
                            result[ip][pfx].append(duration)
                        
                        announced[ip].pop(pfx)
                        withdraw[ip].pop(pfx)
    # print(result["192.65.185.140"]["64.192.0.0/24"])
    # printout = dict(result)
    # with open("out4.json","w") as fp:
    #     json.dump(printout, fp)
    return dict(result)
    

    

# The main function will not be run during grading.
# You may use it however you like during testing.
#
# NB: make sure that check_solution.py runs your
#     solution without errors prior to submission
if __name__ == '__main__':
    # do nothing
    pass
