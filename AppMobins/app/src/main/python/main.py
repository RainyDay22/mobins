ret_val = "hey Python"

try:
    from mobile_insight import monitor, analyzer
    # from mobile_insight import fake, more_fake # this causes error, app quits, not log msg
except ImportError as e:
    # print(e) #doesn't really execute since console ops aren't implemented
    ret_val = e #seems to not run quite right as a work around to print


def main():
    return ret_val