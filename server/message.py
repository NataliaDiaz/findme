import simplejson as json

from handlers import *

class StalkerMessage(object):

    MSG_UPDATE = 1
    MSG_LOGIN = 2
    MSG_REGISTER_POI = 3
    MSG_POI_REQUEST = 4
    MSG_LOGOUT = 10
    MSG_FRIEND_REQUEST = 11
    MSG_SET_STALKEES = 13

    MSG_TO_TYPE = {"update":MSG_UPDATE,
                   "login":MSG_LOGIN,
                   "friend_request":MSG_FRIEND_REQUEST,
                   "register_poi":MSG_REGISTER_POI,
                   "poi_request":MSG_POI_REQUEST,
                   "set_stalkees":MSG_SET_STALKEES}


    def __init__(self, sender, hash_pwd, mac, timestamp, msg_type):

        self._sender = sender
        self._hash_pwd = hash_pwd
        self._mac = mac
        self._timestamp = timestamp
        self._msg_type = msg_type
        
    @property
    def msg_type(self):
        return self._msg_type

    # Implement this
    def get_handler(self):
        pass

    def to_json(self):

        msg_type_str = ""

        for msg_type, msg_int in self.MSG_TO_TYPE.items():
            if msg_int == self._msg_type:
                msg_type_str = msg_type
                break
        
        return json.dumps( {"sender":self._sender,
                    "hash":self._hash_pwd,
                    "mac":self._mac,
                    "timestamp":self._timestamp,
                    "action_type":msg_type_str,
                    "action": self._action_to_json()}
                    )
        

    # Implement this
    def _action_to_json(self):
        return {}
        pass

"""
Update message
"""
class UpdateMessage(StalkerMessage):

    def __init__(self, sender, hash_pwd, mac, timestamp, msg_type, updater_name, coords, floor, accuracy):
        super(UpdateMessage, self).__init__(sender, hash_pwd, mac, timestamp,msg_type)
        self._updater_name = updater_name
        self._coords = coords
        self._accuracy = accuracy;
        self._floor = floor

    def _action_to_json(self):
        return {"updater":self._updater_name,
                "coords":self._coords,
                "accuracy":self._accuracy,
                "floor":self._floor}

    def get_handler(self):
        return UpdateMessageHandler(self)

"""
Set stalkees message
"""
class SetStalkeesMessage(StalkerMessage):

    def __init__(self, sender, hash_pwd, mac, timestamp, msg_type, stalkees):
        super(SetStalkeesMessage, self).__init__(sender, hash_pwd, mac, timestamp,msg_type)
        self._stalkees = stalkees

    def _action_to_json(self):
        return {"stalkees":stalkees}

    def get_handler(self):
        return SetStalkeesMessageHandler(self)

"""
New POI message
"""
class NewPOIMessage(StalkerMessage):

    def __init__(self, sender, hash_pwd, mac, timestamp, msg_type, name, coords, floor, img_url):
        super(NewPOIMessage, self).__init__(sender, hash_pwd, mac, timestamp,msg_type)
        print "POI sender is", self._sender
        self._name = name
        self._coords = coords
        self._img_url = img_url
        self._floor = floor

    def _action_to_json(self):
        return {"name":self._name,
                "coords":self._coords,
                "floor":self._floor,
                "img_url":self._img_url}

    def get_handler(self):
        return NewPOIMessageHandler(self)
                                            
"""
POI Request message
"""
class POIRequestMessage(StalkerMessage):

    def __init__(self, sender, hash_pwd, mac, timestamp, msg_type):
        super(POIRequestMessage, self).__init__(sender, hash_pwd, mac, timestamp,msg_type)
        

    def _action_to_json(self):
        return {}

    def get_handler(self):
        return POIRequestMessageHandler(self)

"""
Login message
"""
class LoginMessage(StalkerMessage):

    def __init__(self, sender, hash_pwd, mac, timestamp, msg_type, password):
        super(LoginMessage, self).__init__(sender, hash_pwd, mac, timestamp,msg_type)
        self._password = password

    def _action_to_json(self):
        return {"password":self._password}


class MessageCreator(object):


    def create_message(self, data):

        # Identify message type
        print data
        print "-----------"
        
        json_data = json.loads(data)

        if json_data == None:
            print "Unable to parse json data!"
            return None

        # TODO: Convert to floats/ints etc.
        sender_str = json_data["sender"]
        hash_pwd_str = json_data["hash"]
        mac_str = "FF00FF00" #json_data["mac"]
        timestamp_str = json_data["timestamp"]
        
        msg_type_str = json_data["action_type"]
        msg_type = StalkerMessage.MSG_TO_TYPE[msg_type_str]
        message = None
        action_json = json_data["action"]

        print "Got message of type", msg_type_str, "number", msg_type

        if msg_type == StalkerMessage.MSG_UPDATE:

            updater_name_str = action_json["updater"]
            coords_tuple = action_json["coords"]
            floor_str = action_json["floor"]
            accuracy_str = action_json["accuracy"]
            
            message = UpdateMessage(sender_str, hash_pwd_str, mac_str,
                                    timestamp_str, msg_type, updater_name_str,
                                    coords_tuple, int(floor_str), float(accuracy_str) )

            
        elif msg_type == StalkerMessage.MSG_LOGIN:
            pass

        elif msg_type == StalkerMessage.MSG_SET_STALKEES:

            stalkees = action_json["stalkees"]

            message = SetStalkeesMessage(sender_str, hash_pwd_str, mac_str,
                                    timestamp_str, msg_type, stalkees)

        elif msg_type == StalkerMessage.MSG_REGISTER_POI:

            name = action_json["name"]
            coords = action_json["coords"]
            floor = action_json["floor"]
            img_url = action_json["img_url"]

            message = NewPOIMessage(sender_str, hash_pwd_str, mac_str,
                                    timestamp_str, msg_type,
                                    name, coords, int(floor), img_url)

        elif msg_type == StalkerMessage.MSG_POI_REQUEST:
            message = POIRequestMessage(sender_str, hash_pwd_str, mac_str,
                                    timestamp_str, msg_type)

            
        return message

