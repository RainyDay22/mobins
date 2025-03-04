ret_val= "=====this is an unchanged global variable====="
ANDROID_SHELL = "/system/bin/sh" ##
src = None

try:
    """                                                                                                                                                                   
    Cellular event logging main app                                                                                                                                       
    """
    import os
    import sys
    import shutil
    import traceback
    from mobile_insight.analyzer import LogAnalyzer
    from datetime import datetime

except ImportError as e:
    ret_val = repr(e) #seems to not run quite right as a work around to print

def read_milog( toanalyze_path):
    global ret_val
    global src

    try:
        print("==== hello world ====", toanalyze_path) #std testing

        log_dir = "/data/data/com.example.appmobins/files"  #hardcoded

        loggingAnalyzer = LogAnalyzer(None)
        loggingAnalyzer.AnalyzeFile(toanalyze_path, None)
        log_outputs = loggingAnalyzer.msg_logs #list of dictionaries of length 3, keys "TypeID", "Timestamp", "Payload"

        # print(type(woop), len(woop[0]), woop[:5]) #debugging
        ret_val = log_outputs

    except Exception:
        ret_val= traceback.format_exc()

    return ret_val
