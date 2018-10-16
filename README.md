# Setup guide for application use

## 1) Connect your phone and your PC.

If you're at home, just connect your phone and your computer to your Wi-Fi.
Else, you can :

- use your phone as a modem then connect your computer to it.

- connect your computer and your phone to a router.

## 2) Start RabbitMQ's communication tool.

### For windows

In a terminal, go to the following directory `[…] \RabbitMQ\rabbitmq_server-3.7.8\sbin` with `cd` command.
Then start RabbitMQ server with this command `.\rabbitmq-server.bat`.

### For linux

In linux controle if RabbitMQ is running by running this command: `sudo service rabbitmq-server`

For more installation information see the python source code [here](https://github.com/tpusmb/python-twin-gan/blob/master/README.md)

## 3) Start the Python server.

Run `server.py` and then wait until the following lines ar displayed:

	server.py [INFO] Thread […] \human_to_cat_128\128 load !
	server.py [INFO] Thread […] \twingan_256\256 load !

The server is finally ready and is listening.

You can found the python source code [here](https://github.com/tpusmb/python-twin-gan.git)
