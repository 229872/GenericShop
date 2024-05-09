import './App.css'
import NavigationBar from './pages/NavigationBar';
import './utils/i18n'; 
import { Toaster } from 'sonner'
import RoutingWithSessionDialogs from './components/singleuse/RoutingWithSessionDialogs';



function App() {

  return <>
    <Toaster position='top-right' richColors style={{marginTop: '45px'}} />
    <NavigationBar />
    <RoutingWithSessionDialogs />
  </> 
}

export default App;
