#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

FROM palantirtechnologies/circle-spark-base:0.2.3

# Install pyenv
RUN curl -L https://github.com/pyenv/pyenv-installer/raw/master/bin/pyenv-installer | bash
ENV PATH="$CIRCLE_HOME/.pyenv/shims:$CIRCLE_HOME/.pyenv/bin:$PATH"

# Must install numpy 1.11 or else a bunch of tests break due to different output formatting on e.g. nparray
# A version I've tested earlier that I know it breaks with is 1.14.1
RUN mkdir -p $(pyenv root)/versions \
  && ln -s $CONDA_ROOT $(pyenv root)/versions/our-miniconda \
  && $CONDA_BIN create -y -n python2 -c anaconda -c conda-forge python==2.7.18 numpy=1.14.0 pyarrow==0.12.1 pandas nomkl \
  && $CONDA_BIN create -y -n python3 -c anaconda -c conda-forge python=3.6 numpy=1.14.0 pyarrow==0.12.1 pandas nomkl \
  && $CONDA_BIN clean --all

RUN pyenv global our-miniconda/envs/python2 our-miniconda/envs/python3 \
  && pyenv rehash

RUN PYENV_VERSION=our-miniconda/envs/python2 $CIRCLE_HOME/.pyenv/shims/pip install unishark "unittest-xml-reporting<3"
RUN PYENV_VERSION=our-miniconda/envs/python3 $CIRCLE_HOME/.pyenv/shims/pip install unishark unittest-xml-reporting

# Conda 'activate' should take precedence over pyenv's (pyenv/pyenv-virtualenv#270).
# In run-pip-tests, we do 'source activate /path'. Conda's activate is fine with that, but not pyenv's.
RUN mkdir $CIRCLE_HOME/.bin
RUN ln -s $CONDA_ROOT/bin/activate $CIRCLE_HOME/.bin/activate
RUN ln -s $CONDA_ROOT/bin/deactivate $CIRCLE_HOME/.bin/deactivate
ENV PATH="$CIRCLE_HOME/.bin:$PATH"
