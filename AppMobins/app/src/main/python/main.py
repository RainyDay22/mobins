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
    from mobile_insight.monitor import OnlineMonitor
    # from logging_analyzer import LoggingAnalyzer
    #removed all vars for log_enable
    from java.lang import Runnable ##pykot
    import java
    from datetime import datetime

except ImportError as e:
    ret_val = repr(e) #seems to not run quite right as a work around to print

def halt_daemon():
    print("=========halt start=========")
    global src
    if src==None: return

    src._pause_collection()
    print("=========halt end=========")


class AndroidStream: ###pykot

    def __init__(self, activity):
        self.activity = activity

    def write(self, message):
        if message.strip():  # Ignore empty lines
            # Pass message to the activity to update the UI

            # class Run_output(java.dynamic_proxy(Runnable)):
            #     def __init__(self, activity):
            #         super().__init__()
            #         self.activity = activity
            #     def run(self):
            #         self.activity.output(message)
            # run_me = Run_output(self.activity)
            # self.activity.runOnUiThread(run_me) #does a weird runonUithread sandwich, works the smae as line below

            self.activity.output(message) ##trial?? this just skips all the prints

    def flush(self):
        pass  # No-op to satisfy the file-like interface


def main():
    global ret_val
    global src

    try:
        #stream redirect
        sys.stdout = AndroidStream(activity)
        sys.stderr = AndroidStream(activity)

        print("=========start main=========") #std testing
        print(activity.access_log_size(),"***", activity.access_log_type())

        # pykot_test() #tested sequential printing

        date_time=datetime.now().strftime('%Y-%m-%d_%H:%M:%S')

        src = OnlineMonitor()
        src.set_skip_decoding(False)
        log_dir = "/data/data/com.example.appmobins/files"  #hardcoded
        src.set_log_directory(log_dir)
        #log file name to be diaglog_yyyymmdd_hhmmss.mi2log
        log_name = "diaglog_"+date_time+".mi2log"
        log_file_path = os.path.join(log_dir, log_name) #mi2log attempt
        src.save_log_as(log_file_path)

        src.set_log_cut_size(activity.access_log_size()/1000) #convert kilobytes to megabytes

        if activity.access_log_type() == "All":
            src.enable_log_all()
        else:
            src.enable_log(activity.access_log_type())
    # # #removed all specific log_enable mentions
    #     src.enable_log_all()


    # loggingAnalyzer = LoggingAnalyzer(plugin_config)
    # loggingAnalyzer.set_source(src)
        src.run()

    except Exception:
        ret_val= traceback.format_exc()

    return ret_val