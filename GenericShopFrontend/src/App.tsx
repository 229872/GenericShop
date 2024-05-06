import './App.css'
import { BrowserRouter, Route, Routes } from "react-router-dom";
import NavigationBar from './pages/NavigationBar';
import './utils/i18n'; 

function App() {
  return <>
    <BrowserRouter>
      <NavigationBar />
      <Routes>
        
      </Routes>
    </BrowserRouter>
  </> 
}

export default App;
