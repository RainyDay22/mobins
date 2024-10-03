try:
    from mobile_insight import monitor, analyzer
    # from mobile_insight import fake, more_fake # this causes error, app quits, not log msg

    #!/usr/bin/python
    # Filename: lte-measurement-example
    import os
    import sys

    # Import MobileInsight modules
    from mobile_insight.analyzer import *
    from mobile_insight.monitor import OfflineReplayer

except ImportError as e:
    # print(e) #doesn't really execute since console ops aren't implemented
    ret_val = e #seems to not run quite right as a work around to print


def main():
    ret_val = "hey Python"
    replayer = OfflineReplayer()
    return ret_val
    # if len(sys.argv) < 3:
    #     ret_val+="Error: please specify physical port name and baudrate."
    #     # print((__file__, "SERIAL_PORT_NAME BAUNRATE"))
    #     sys.exit(1)
    #
    # # Initialize a DM monitor
    # src = OnlineMonitor()
    # src.set_serial_port(sys.argv[1])  # the serial port to collect the traces
    # src.set_baudrate(int(sys.argv[2]))  # the baudrate of the port
    #
    # dumper = MsgLogger()
    # dumper.set_source(src)
    # dumper.set_decoding(MsgLogger.XML)  # decode the message as xml
    #
    # nas_analyzer = LteNasAnalyzer()
    # nas_analyzer.set_source(src)
    #
    # # save the analysis result. All analyzers share the same output file.
    # dumper.set_log("nas-analyzer-example.txt")
    # nas_analyzer.set_log("nas-analyzer-example.txt")
    #
    # # Start the monitoring
    # src.run()
    # return ret_val