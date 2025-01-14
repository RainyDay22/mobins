ret_val= "this is an unchanged global variable"
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
    from mobile_insight.monitor import OnlineMonitor
    # from logging_analyzer import LoggingAnalyzer
    #removed all vars for log_enable


except ImportError as e:
    ret_val = repr(e) #seems to not run quite right as a work around to print

def halt_daemon():
    print("halt start")
    global src
    if src==None: return

    src._pause_collection()
    print("halt end")

def main():
    global ret_val
    global src

    try:
        # print("helloworld") #std testing

        src = OnlineMonitor()
        src.set_skip_decoding(False)
        log_dir = "/data/data/com.example.appmobins/files"  #hardcoded
        src.set_log_directory(log_dir)
    # #removed all specific log_enable mentions
        src.enable_log_all()
    # loggingAnalyzer = LoggingAnalyzer(plugin_config)
    # loggingAnalyzer.set_source(src)
        src.run()

    except Exception:
        ret_val= traceback.format_exc()

    return ret_val