#taken from chaquopy console

from io import TextIOBase

class ConsoleOutputStream(TextIOBase):
    """Passes each write to the underlying stream, and also to the given method, which must take
    a single string argument.
    """
    def __init__(self, stream, obj, method_name):
        self.stream = stream
        self.method = getattr(obj, method_name)

    # def __init__(self, stream, obj, method_name, method_arg):
    #     self.stream = stream
    #     self.method = getattr(obj, method_name)
    #     self.m_arg = method_arg

    def __repr__(self):
        return f"<ConsoleOutputStream {self.stream}>"

    def __getattribute__(self, name):
        # Forward all attributes that have useful implementations.
        if name in [
            "close", "closed", "flush", "writable",  # IOBase
            "encoding", "errors", "newlines", "buffer", "detach",  # TextIOBase
            "line_buffering", "write_through", "reconfigure",  # TextIOWrapper
        ]:
            return getattr(self.stream, name)
        else:
            return super().__getattribute__(name)

    def write(self, s):
        # Pass the write to the underlying stream first, so that if it throws an exception, the
        # app crashes in the same way whether it's using ConsoleOutputStream or not (#5712).
        result = self.stream.write(s)
        self.method(s)#, self.arg)
        return result