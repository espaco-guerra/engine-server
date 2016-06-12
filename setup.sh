#!/usr/bin/env bash
set -e

MY_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

cd ${MY_DIR}

if [[ -z `which ruby` ]]; then
  echo "Please ensure ruby is installed and in your path" && exit 1
fi

if [[ -z `which curl` ]]; then
  echo "Please ensure the command line utility curl is installed and in your path" && exit 1
fi

if [[ ! -z `uname -a | grep Linux` ]] && [[ -z `which apt-get` ]]; then
  echo "Unfortunately, this project is only setup for aptitude based linuxes. You'll have to open this file (${BASH_SOURCE}) and ensure all apt-get calls are adjusted to your package manager." && exit 1
fi

if [[ ! -z `uname -a | grep Darwin` ]] && [[ -z `which brew` ]]; then
  echo "Installing brew..."
  ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
fi

if [[ -z `( (which javac &> /dev/null) && javac -version 2>&1) | grep "1.8"` ]]; then
  echo "Installing JVM 1.8..."
  if [[ ! -z `uname -a | grep Darwin` ]]; then
    (brew --version > /dev/null) && (brew tap caskroom/cask > /dev/null) && (brew install brew-cask > /dev/null) && (brew cask install java)
  fi
  if [[ ! -z `uname -a | grep Linux` ]]; then
    (apt-get install python-software-properties) && (add-apt-repository ppa:webupd8team/java) && (apt-get update) && (apt-get install -y oracle-java8-installer)
  fi
fi

mkdir -p ${MY_DIR}/bin
mkdir -p ${MY_DIR}/tmp
mkdir -p ${MY_DIR}/lib
if [[ ! -f ${MY_DIR}/bin/lein ]]; then
  echo "Installing local lein..."
  curl --silent "https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein" > ${MY_DIR}/bin/lein
fi
chmod +x ${MY_DIR}/bin/lein
${MY_DIR}/bin/lein &> /dev/null
echo "Installing dependencies..."
${MY_DIR}/bin/lein deps &> /dev/null
