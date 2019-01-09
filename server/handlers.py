import _mysql
import simplejson as json

class MessageHandler(object):

    def __init__(self, message):

        self._message = message

    # Implement this
    def handle_message(self, con):
        return None

"""

"""
class UpdateMessageHandler(MessageHandler):

    def __init__(self, message):
        super(UpdateMessageHandler, self).__init__(message)

    def handle_message(self, con):

        user = self._message._sender
        lat = self._message._coords[0]
        lng = self._message._coords[1]
        floor = self._message._floor
        acc = self._message._accuracy
        try:
            print "Update for '%s' with coords %f %f" % (user, lat, lng)
            #cursor = con.cursor()
            con.query("""UPDATE USER SET longitude=%f, latitude=%f,
                         accuracy=%f, floor=%i WHERE username='%s';
                      """ % (lat, lng, acc, floor, user) )
            con.commit()
        except _mysql.Error, e:
            print "Cannot handle update message!", e
            return None

        
        # Get all user's friends
        friends = []

        try:
            con.query("SELECT user2 FROM SUBSCRIBER where user1='%s'" % (user))
            results = con.store_result()
            
            while True:
                row = results.fetch_row()
                if not row: break
                friend = row[0][0]
                friends.append(friend)

            print "Friends of", user,"are: ", friends
            
        except _mysql.Error, e:
            print "Cannot get friends of user %s message!" % (user), e


        friend_positions = []

        # Get friend's positions
        try:
            for friend in friends:
                con.query("""SELECT latitude, longitude, floor, accuracy, timestamp FROM USER
                             WHERE username='%s'""" % (friend))

                results = con.store_result()

                while True:
                    row = results.fetch_row()
                    if not row: break
                    coords = [float(row[0][0]), float(row[0][1])]
                    floor = int(row[0][2])
                    accuracy = float(row[0][3])
                    timestamp = int(row[0][4])

                    friend_positions.append( {"user":friend, "coords":coords, "floor":floor,
                                              "accuracy": accuracy, "timestamp": timestamp })

        except _mysql.Error, e:
            print "Cannot get friends of user %s!" % (user), e


        json_reply = json.dumps({"sender":"root",
                                 "hash" : "root",
                                 "mac" : "123456FF",
                                 "timestamp" : "42",
                                 "action_type" : "update",
                                 "action" : friend_positions
                                })
        
        
        return json_reply
"""

"""
class SetStalkeesMessageHandler(MessageHandler):

    def __init__(self, message):
        super(SetStalkeesMessageHandler, self).__init__(message)

    def handle_message(self, con):

        # Delete previous stalkees, and re-insert them
        user = self._message._sender
        stalkees = self._message._stalkees
        success = True
        error_msg = ""
        
        try:
            print "Update for with stalkees", stalkees
            
            #cursor = con.cursor()
            con.query("""DELETE FROM SUBSCRIBER WHERE
                         user1='%s';
                      """ % (user) )

            for stalkee in stalkees:
                print "Stalker is %s, stalkee is %s" % (user, stalkee)
                con.query("""INSERT INTO SUBSCRIBER (user1, user2) values('%s', '%s');""" % (user, stalkee))
        except _mysql.Error, e:
            print "Cannot handle set_stalkees message!", e
            error_msg = e
            success = False
        
        json_reply = json.dumps({"sender":"root",
                                 "hash" : "root",
                                 "mac" : "123456FF",
                                 "timestamp" : "42",
                                 "action_type" : "set_stalkees",
                                 "action" : {
                                    "reply" : success,
                                    "error" : error_msg
                                 }
                                })
        
        
        return json_reply

"""

"""
class NewPOIMessageHandler(MessageHandler):

    def __init__(self, message):
        super(NewPOIMessageHandler, self).__init__(message)

    def handle_message(self, con):

        user = self._message._sender
        name = self._message._name
        coords = self._message._coords
        floor = self._message._floor
        img_url = self._message._img_url

        success = True
        error_msg = ""
        
        try:
            con.query("""INSERT INTO POI(user, latitude, longitude, floor, name, img_url)
                         values('%s', %f, %f, %i, '%s', '%s');""" %
                      (user, coords[0], coords[1], floor, name, img_url))

        except _mysql.Error, e:
            print "Cannot handle New POI message!", e
            error_msg = e
            success = False


        json_reply = json.dumps({"sender":"root",
                                 "hash" : "root",
                                 "mac" : "123456FF",
                                 "timestamp" : "42",
                                 "action_type" : "set_stalkees",
                                 "action" : {
                                    "reply" : success,
                                    "error" : error_msg
                                 }
                                })

        return json_reply

        
"""

"""
class POIRequestMessageHandler(MessageHandler):

    def __init__(self, message):
        super(POIRequestMessageHandler, self).__init__(message)

    def handle_message(self, con):

        user = self._message._sender
        friends = []

        # Get all user's friends
        try:
            con.query("SELECT user2 FROM SUBSCRIBER where user1='%s'" % (user))
            results = con.store_result()
            
            while True:
                row = results.fetch_row()
                if not row: break
                friend = row[0][0]
                friends.append(friend)

            print "Friends of", user,"are: ", friends
            
        except _mysql.Error, e:
            print "Cannot get friends of user %s message!" % (user), e


        poi = []

        # Get new positions
        try:
            for friend in friends:
                con.query("""SELECT latitude, longitude, floor, name, img_url FROM POI
                             WHERE user='%s'""" % (friend))

                results = con.store_result()

                while True:
                    row = results.fetch_row()
                    print "Got row: ", row
                    if not row: break
                    coords = [float(row[0][0]), float(row[0][1])]
                    floor = int(row[0][2])
                    name = row[0][3]
                    img_url = row[0][4]

                    poi.append( {"user":friend, "coords": coords, "floor":floor,
                                 "name":name, "img_url":img_url})


        except _mysql.Error, e:
            print "Cannot get friend %s updated position" % (friend)
            
        json_reply = json.dumps({"sender":"root",
                                 "hash" : "root",
                                 "mac" : "123456FF",
                                 "timestamp" : "42",
                                 "action_type" : "poi_request",
                                 "action" : {
                                    "poi" : poi
                                 }
                                })

        return json_reply
            
