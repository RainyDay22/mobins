ret_val= "=====No errors occurred====="
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
        loggingAnalyzer = LogAnalyzer(None)
        loggingAnalyzer.AnalyzeFile(toanalyze_path, None)
        log_outputs = loggingAnalyzer.msg_logs #list of dictionaries of length 3, keys "TypeID", "Timestamp", "Payload"

        # print(type(log_outputs), len(log_outputs[0]), log_outputs[:5]) #debugging
        ret_val = log_outputs

    except Exception:
        ret_val= traceback.format_exc()

    return ret_val
