import './App.css'
import NavigationBar from './components/singleuse/NavigationBar';
import './utils/i18n'; 
import { Toaster } from 'sonner'
import RoutingWithSessionDialogs from './components/singleuse/RoutingWithSessionDialogs';
import { useState } from 'react';
import Progress from './components/singleuse/Progress';



function App() {
  const [ loading, setLoading ] = useState<boolean>(false)

  return <>
    <Toaster position='top-left' richColors closeButton expand style={{ marginTop: '60px' }}  offset={'40px'} />
    <NavigationBar />
    <Progress loading={loading} />
    <RoutingWithSessionDialogs setLoading={setLoading} />
  </> 
}

export default App;
