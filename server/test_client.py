
import socket
import _mysql
import httplib, urllib

from message import MessageCreator, StalkerMessage
from handlers import *

def test():

    #_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    #_socket.connect( ('127.0.0.1', 4242) )
    #_socket.send('hello world!')

    update_message = '''{ "sender" : "norris",
                      "hash" : "12345690",
                      "mac" : "1BAC22FF",
                      "address" : "192.168.1.1",
                      "timestamp" : "01012014133700",
                      "action_type" : "update",
                      "action" : { "updater" : "Mr. Coffee",
                                   "coords" : [1.04, 0.42],
                                   "floor" : "1",
                                   "accuracy" : "1.23"
                                 } }'''

    set_stalkees_message = '''
                    { "sender" : "norris",
                      "hash" : "12345690",
                      "mac" : "1BAC22FF",
                      "address" : "192.168.1.1",
                      "timestamp" : "01012014133700",
                      "action_type" : "set_stalkees",
                      "action" : { "stalkees" : [
                                   "arnold", "seagal"]
                                 }
                      }'''

    register_poi_message = '''
                    { "sender" : "norris",
                      "hash" : "12345690",
                      "mac" : "1BAC22FF",
                      "address" : "192.168.1.1",
                      "timestamp" : "01012014133700",
                      "action_type" : "register_poi",
                      "action" : { "name" : "Test point",
                                   "coords" : [42.42, 11.11],
                                   "floor" : "1",
                                   "img_url" : "http://i.imgur.com/PlnFq8E.jpg"
                                 }
                      }'''

    poi_request_message = '''
                    { "sender" : "ford",
                      "hash" : "12345690",
                      "mac" : "1BAC22FF",
                      "address" : "192.168.1.1",
                      "timestamp" : "01012014133700",
                      "action_type" : "poi_request",
                      "action" : {}
                      }'''

    #params = urllib.urlencode({'spam': 1, 'eggs': 2, 'bacon': 0})
    headers = {"Content-type": "application/x-www-form-urlencoded",
                "Accept": "text/plain"}
    conn = httplib.HTTPConnection("127.0.0.1:4242")
    conn.request("POST", "/", update_message, headers)
    response = conn.getresponse()
    print response.status, response.reason

    data = response.read()
    print "DATA:", data
    #print urllib.urlencode(update_message)
    conn.close()


##    message_creator = MessageCreator()
##
##    msg = message_creator.create_message(json_message)
##    msg_str = msg.to_json()
##    print msg_str
##
##    msg2 = message_creator.create_message(msg_str)
##    print msg2.to_json()
##
##    handler = msg2.get_handler()
##
##    _con = _mysql.connect('127.0.0.1', 'stalker', '', 'stalker')
##
##    return_message = handler.handle_message(_con)

#    _socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    #_socket_reader = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    #_socket_reader.bind( ('127.0.0.1', 4200) )
    #_socket_reader.listen(5)
    
#    _socket.connect( ('albin.abo.fi', 4242) )

#    _socket.send(update_message)
    #(client, address) = _socket_reader.accept()
#    data, server = _socket.recvfrom(2048)
    #print "DATA:", data
   
#    _socket.send(poi_request_message)
#    data, server = _socket.recvfrom(2048)

  
    #print "DATA: ",data


#    _socket.close()

                
if  __name__ =='__main__':
    test()
