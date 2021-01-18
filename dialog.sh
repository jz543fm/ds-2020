#!/bin/bash

# using dialog to create a menu

# Script uses while loop with a constant true value = endless loop, after every function, script returns to displaying menu

# Menu dialog includes a Cancel button, so the script checks the exit status of the dialog command in case the use presses
# the cancel button to exit. After cancel button then is called clear function to clear previous screen.

# Script uses the mktemp command to create 2 temporary files for holding data for the dialog commands.

# The first one, $temp, is used to hold the output of the df, whoseon a meminfo commands, so they can be displayed in the
# textbox dialog. The 2nd temporary file, $temp2, is used to hold the selection value from the main dialog.



temp=$(mktemp -t test.XXXXXX)
temp2=$(mktemp -t test2.XXXXXX)

function get_transactions()
{
  echo ""
  echo "    [ $ ]  TRANSACTIONS "
  echo " ________________________________________________"
  echo ""
  curl -X GET $SERVER_HOSTNAME/transactions 2>/dev/null | sed 's/^/    /g'
  echo ""
}



function get_blocks()
{
    echo ""
    echo "  [-]   BLOCKS  [-]<->[-] "
    echo " ________________________________________________"
    echo ""
    curl -X GET $SERVER_HOSTNAME/blocks 2>/dev/null | sed 's/^/    /g'
    echo ""
}

function get_peers(){
    echo ""
    echo "  [ --> ] CONNECTIONS  [-]<---->[-] "
    echo " ________________________________________________"
    echo ""
    curl -X GET $SERVER_HOSTNAME/peers 2>/dev/null | sed 's/^/    /g'
    echo ""

}

function connect_to_node(){
  echo ""
  echo "  [ --> ] CONNECT TO NODE  [-]<---->[-] "
  echo " ________________________________________________"
  echo ""
  echo -n "   Enter port of the remote peer: "
  read peer_port
  echo ""
  echo -n "   [ i ]  "
  curl -X POST $SERVER_HOSTNAME/connect/?port="$peer_port"
  echo ""
}

function generate_transaction(){
    echo ""
    echo "  [ $ ] GENERATE TRANSACTION   [-] ---> [-]  "
    echo " ________________________________________________"
    echo ""
    echo -n "   Sender: "
    read sender
    echo -n "   Reveicer: "
    read receiver
    echo -n "   Amount: "
    read amount
    echo ""
    echo -n "   [ i ]  "

    curl  -X POST $SERVER_HOSTNAME/newTransaction/?sender=$sender'&'receiver=$receiver'&'amount=$amount

    echo ""

}

while [ 1 ]

do
	dialog --menu "Sys Admin Menu" 20 30 10 1 "Display disk space" 2 "Display users" 3 "Display memory usage" 0 "Exit" 2> $temp2
	if [ $? -eq 1 ]
	then
		clear
		break

	fi

	selection=$(cat $temp2)

	case $selection in
		1)
			get_transactions ;;
		2)
			get_blocks ;;
		3)
			get_peers ;;
		4)
			connect_to_node ;;
	4)
			connect_to_node ;;
		0)
			break ;;

		*)
			dialog --msgbox "Sorry, invalid selection" 10 30

		esac
	done

	rm -f $temp 2> /dev/null

	rm -f $temp2 2> /dev/null








