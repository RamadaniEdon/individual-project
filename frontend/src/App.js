import logo from './logo.svg';
import './App.css';
import AppBody from './components/AppBody';
import { BrowserRouter as Router } from 'react-router-dom';

function App() {
  return (
    <div className="App">
      <Router>
        <AppBody />
      </Router>
    </div>
  );
}

export default App;
