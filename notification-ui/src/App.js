import logo from './logo.svg';
import './App.css';
import Login from './login/Login';
import { Box, Button, Flex, Text } from '@chakra-ui/react';
import SignUp from './signUp/SignUp';
import { Routes, Route } from 'react-router-dom';
import Preference from './preferences/Preference';

function App() {
  return (
    <Flex width={'100%'} height={'100%'}>
      <Flex width={'10%'}>

      </Flex>
      <Flex width={'80%'} gap={'16px'} direction={'column'}>

        <Flex gap={'16px'}>
          <Routes>
            <Route path='/' element={
              <Flex gap={'16px'} direction={'column'}>
                <Box height={'56px'}></Box>
                <Flex direction={'column'}>
                  <Text fontSize={'3xl'}>User Enrollment</Text>
                  <Text fontSize={'1xl'}>Please follow the steps below to create an account</Text>
                </Flex>
                <Flex gap={'16px'}>
                  <SignUp />
                  <Login />
                </Flex>
              </Flex>} />
            <Route path='/preferences' element={
              <Flex gap={'16px'} direction={'column'}>
                <Box height={'56px'}></Box>
                <Preference />
              </Flex>
            } />
          </Routes>
        </Flex>
      </Flex>
      <Flex width={'10%'}>

      </Flex>
    </Flex>
  );
}

export default App;
