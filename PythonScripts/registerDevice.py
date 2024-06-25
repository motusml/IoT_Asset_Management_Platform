from asyncCommunication import RequestHandler

# asyncController url
url = "http://localhost:9096"

# device name
name = "Device1"

handler = RequestHandler(name, url)
handler.register()
