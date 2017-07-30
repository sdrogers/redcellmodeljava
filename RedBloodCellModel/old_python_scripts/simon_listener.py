"""
Simple listener for use in tests
"""

class Listener(object):
    def __init__(self):
        self.vals = {}

    def publish(self, name, value, time = 0.0,stage=0):
        self.vals[name] = self.vals.get(name, []) + [value]