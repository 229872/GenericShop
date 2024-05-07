import './App.css'
import { BrowserRouter, Route, Routes } from "react-router-dom";
import NavigationBar from './pages/NavigationBar';
import './utils/i18n'; 
import AuthenticationPage from './pages/AuthenticationPage';
import { Toaster } from 'sonner'

function App() {
  return <>
    <Toaster position='top-right' richColors style={{marginTop: '45px'}} />
    <BrowserRouter>
      <NavigationBar />
      <Routes>
        <Route path='/auth' element={<AuthenticationPage />} />
      </Routes>
    </BrowserRouter>
  </> 
}

export default App;
