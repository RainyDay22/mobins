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


class AndroidStream: #redirecting stdout and stderr streams to kotlin output console

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

            self.activity.uiOutput(message) ##trial?? this just skips all the prints

    def flush(self):
        pass  # No-op to satisfy the file-like interface


def format_logtype(UIinput):
    formatted = []

    if UIinput == "5G Control Plane":
        formatted = nr_lvl1 + lte_lvl1
    elif UIinput == "LTE Control Plane":
        formatted = lte_lvl1
    elif UIinput == "LTE PHY":
        formatted = lte_lvl3
    elif UIinput == "LTE Control/Data Plane":
        formatted =lte_lvl1 + lte_lvl2
    elif UIinput == "LTE Control/PHY":
        formatted = lte_lvl1 + lte_lvl3
    elif UIinput == "LTE Control/Data/PHY":
        formatted =lte_lvl1 + lte_lvl2 + lte_lvl3
    elif UIinput == "LTE/3G Control Plane":
        formatted =lte_lvl1 + wcdma + umts
    else:
        print("Incorrect log type: ", UIinput, file=sys.stderr)

    return formatted

def main():
    global ret_val
    global src

    try:
        #stream redirect
        sys.stdout = AndroidStream(activity)
        sys.stderr = AndroidStream(activity)

        print("=========start main=========") #std testing
        print(activity.access_log_size(),"***", activity.access_log_type())

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
            log_list = format_logtype(activity.access_log_type())
            src.enable_log(log_list)


    # loggingAnalyzer = LoggingAnalyzer(plugin_config)
    # loggingAnalyzer.set_source(src)
        src.run()

    except Exception:
        ret_val= traceback.format_exc()

    return ret_val

nr_lvl1 = [
    '5G_NR_RRC_OTA_Packet',
]

lte_lvl1 = [
    'LTE_RRC_OTA_Packet',
    'LTE_RRC_Serv_Cell_Info',
    'LTE_RRC_MIB_Packet',
    'LTE_RRC_MIB_Message_Log_Packet',
    'LTE_NAS_ESM_State',
    'LTE_NAS_ESM_OTA_Incoming_Packet',
    'LTE_NAS_ESM_OTA_Outgoing_Packet',
    'LTE_NAS_EMM_State',
    'LTE_NAS_EMM_OTA_Incoming_Packet',
    'LTE_NAS_EMM_OTA_Outgoing_Packet',
]
lte_lvl2 = [
    'LTE_RLC_UL_Config_Log_Packet',
    'LTE_RLC_DL_Config_Log_Packet',
    'LTE_RLC_UL_AM_All_PDU',
    'LTE_RLC_DL_AM_All_PDU',
    'LTE_MAC_UL_Transport_Block',
    'LTE_MAC_DL_Transport_Block',
]
lte_lvl3 = [
    'LTE_PHY_PDSCH_Packet',
    # 'LTE_PHY_Serv_Cell_Measurement',
    'LTE_PHY_Connected_Mode_Intra_Freq_Meas',
    'LTE_PHY_Connected_Mode_Neighbor_Measurement',
    'LTE_PHY_Inter_RAT_Measurement'
]
wcdma = [
    'WCDMA_RRC_OTA_Packet',
    'WCDMA_RRC_Serv_Cell_Info',
]
umts = [
    'UMTS_NAS_OTA_Packet',
    'UMTS_NAS_GMM_State',
    'UMTS_NAS_MM_State',
    'UMTS_NAS_MM_REG_State',
]