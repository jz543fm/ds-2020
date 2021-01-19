#!/bin/bash


SERVER_HOSTNAME="localhost:8080"


function get_transactions()
{
  echo ""
  echo -e "\e[32mGETTING ALL TRANSACTION THAT HAS BEEN MADE.\e[0m"
  echo ""
  curl -X GET $SERVER_HOSTNAME/transactions 2>/dev/null | sed 's/^/    /g'
  echo ""

}

function get_blocks()
{
    echo ""
    echo -e "\e[32mCREATED BLOCKSSSSS\e[0m"
    echo ""
    curl -X GET $SERVER_HOSTNAME/blocks 2>/dev/null | sed 's/^/    /g'
    echo ""
}

function get_peers()
{
    echo ""
    echo -e "\e[32m GETTING ALL CONNECTIONS{PEERS}\e[0m"
    echo ""
    curl -X GET $SERVER_HOSTNAME/peers 2>/dev/null | sed 's/^/    /g'
    echo ""

}

function connect_to_node()
{
  echo ""
  echo -e "\e[32mCONNECTING TO THE NODE\e[0m"
  echo ""
  echo -n "   Enter port of the remote peer: "
  read peer_port
  echo ""
  echo -n "   [ i ]  "
  curl -X POST $SERVER_HOSTNAME/connect/?port="$peer_port"
  echo ""
}

function generate_transaction()
{
    echo ""
    echo -e "\e[32mGENERATING TRANSACTION:\e[0m"
    echo ""
    echo -n "   Sending node: "
    read sender
    echo -n "   Receiving node: "
    read receiver
    echo -n "   Amount: "
    read amount
    echo ""
    echo -n "   [ i ]  "

    curl  -X POST $SERVER_HOSTNAME/newTransaction/?sender=$sender'&'receiver=$receiver'&'amount=$amount

    echo ""

}


function doAction()
{
  case "$1" in
          1)
            connect_to_node
            ;;
          2)
            get_transactions
            ;;
          3)
            get_blocks
            ;;
          4)
            generate_transaction
            ;;
          5)
            get_peers
            ;;
          6)
            exit 0
            ;;
          *)
            echo  "Invalid option"
            ;;
  esac
}

function show_options()
{


  echo -e "\n"
  date +'Day:%t %A %b/%e %Y %t Time: %t %I:%M:%S %N %P'
  echo -e "\n"
  echo -e "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"
  echo -e "\e[31m< 1 >      Connect to node                      \e[0m"
  echo -e "\e[31m< 2 >      Show transactions                    \e[0m"
  echo -e "\e[31m< 3 >      Show blocks                          \e[0m"
  echo -e "\e[31m< 4 >      Generate transactions                \e[0m"
  echo -e "\e[31m< 5 >      Show nodes                           \e[0m"
  echo -e "\e[31m< 6 >      Disconnect                           \e[0m"
  echo -e ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"

}

while :;
do
  show_options
  echo ""
  echo -n "|| Option || : -> "

  read option
  echo -e "\n"
  echo -e "EOF\n"
  clear
  doAction $option
done






