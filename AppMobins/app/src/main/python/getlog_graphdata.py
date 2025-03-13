ret_val= "=====No errors occurred====="
ANDROID_SHELL = "/system/bin/sh" ##
src = None

try:
    """                                                                                                                                                                   
    Cellular event logging main app                                                                                                                                       
    """
    # import os
    import sys
    import math
    import traceback
    from mobile_insight.monitor import OfflineReplayer
    from mobile_insight.analyzer import LteDlRetxAnalyzer

except ImportError as e:
    ret_val = repr(e)

def getlog_graphdata(tograph_path, bucketSize=1):
    global ret_val
    global src

    try:
        src = OfflineReplayer()
        src.set_input_path(tograph_path)

        lteAnalyzer = LteDlRetxAnalyzer()
        lteAnalyzer.set_source(src)

        src.run()
        collected = lteAnalyzer.bearer_entity

        mac_delay = []
        mac_acc = 0.0
        mac_delay_sample = 0

        rlc_delay = []
        rlc_acc = 0.0
        rlc_delay_sample = 0

        offset = bucketSize//2

        for _, bearer in lteAnalyzer.bearer_entity.items():
            for item in bearer.mac_retx:
                bucket = (item['mac_retx']//bucketSize)*bucketSize+offset
                mac_delay.append(bucket)
                mac_acc += item['mac_retx']
            mac_delay_sample += len(bearer.mac_retx)

            for item in bearer.rlc_retx:
                bucket = (item['rlc_retx']//bucketSize)*bucketSize+offset
                rlc_delay.append(bucket)
                rlc_acc += item['rlc_retx']
            rlc_delay_sample += len(bearer.rlc_retx)
        #

        frequency = {x:mac_delay.count(x) for x in mac_delay}
        mac_elem = [str(e) for e in frequency.keys()] if mac_delay_sample > 0 else ["0.0"]
        mac_freq = [str(f) for f in frequency.values()] if mac_delay_sample > 0 else ["0"]

        frequency = {x:rlc_delay.count(x) for x in rlc_delay}
        rlc_elem = [str(e) for e in frequency.keys()] if rlc_delay_sample > 0 else ["0.0"]
        rlc_freq = [str(f) for f in frequency.values()] if rlc_delay_sample > 0 else ["0"]

        print( rlc_elem, rlc_freq)

        avg_mac_delay = round(float(mac_acc) / mac_delay_sample, 2) if mac_delay_sample > 0 else 0.0
        avg_rlc_delay = round(float(rlc_acc) / rlc_delay_sample, 2) if rlc_delay_sample > 0 else 0.0
        #
        # print("Average MAC retx delay is: ", avg_mac_delay)
        # print("Average RLC retx delay is:", avg_rlc_delay)

        ret_val = ([",".join(mac_elem) ,",".join(mac_freq), str(avg_mac_delay),
                   ",".join(rlc_elem) ,",".join(rlc_freq),str(avg_rlc_delay)])
        ret_val = "/".join(ret_val)

    except Exception:
        ret_val= traceback.format_exc()

    return ret_val
