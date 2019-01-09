import socket
import _mysql

import BaseHTTPServer
import SocketServer
import urllib2

from message import *

global _con = None

class MyHandler(BaseHTTPServer.BaseHTTPRequestHandler):

    def do_POST(self):
        self.data_string = self.rfile.read(int(self.headers['Content-Length']))
        print urllib2.unquote(self.data_string)
        self.send_response(200)
        self.end_headers()
        print "DID POST!"
        return_message = self._handle_data(self.data_string)
        self.wfile.write(return_message)
        ##data = simplejson.loads(self.data_string)
        ##print len(data)

    def _handle_data(self, data):

        # Initialize a message creator and crate a message from
        # json string
        creator = MessageCreator()
        message = creator.create_message(data)

        if message == None:
            print "Unable to create message from json!"
            return

        handler = message.get_handler()
        
        return_message = handler.handle_message(_con)

        return return_message
	
	


class StalkerServer(object):

    def __init__(self, host='127.0.0.1', port=4242):

        self._port = port
        self._host = host
        #self._socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        
        self._server_class = BaseHTTPServer.HTTPServer
       
       
        

    def run(self):


  
        #self._socket.bind((self._host, self._port))
        #self._socket.listen(25)
        
        httpd = self._server_class( (self._host, self._port), MyHandler)
        
        try:
	    httpd.serve_forever()
	except KeyboardInterupt:
	    pass
	httpd.server_close()

        #while True:

            #client, address = self._socket.accept()
            
            #while True:
                #data = client.recv(2048) # Verify this 2048
                #if not data:
                #    break
                #return_message = self._handle_data(data)
                #print "Client", client, "Address", address
                #client.send(return_message)
                

                
            #client.close()

        #self._socket.close()

    def _handle_data(self, data):

        # Initialize a message creator and crate a message from
        # json string
        creator = MessageCreator()
        message = creator.create_message(data)

        if message == None:
            print "Unable to create message from json!"
            return

        handler = message.get_handler()
        
        return_message = handler.handle_message(self._con)

        return return_message

        

        
        
        
    


    
if  __name__ =='__main__':
    server = StalkerServer()
    try:
       _con = _mysql.connect('127.0.0.1', 'stalker', '', 'stalker')
    except _mysql.Error, e:
        print "Cannot connect to mysql!"
    server.run()
