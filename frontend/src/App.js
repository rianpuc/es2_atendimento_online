import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import ContatoList from './components/ContatoList';
import ContatoForm from './components/ContatoForm';
import CompromissoList from './components/CompromissoList';
import CompromissoForm from './components/CompromissoForm';
import ExameList from './components/ExameList';
import ExameForm from './components/ExameForm';
import AtendimentoList from './components/AtendimentoList';
import AtendimentoForm from './components/AtendimentoForm';
import './App.css';
import ProfissionalSaudeList from './components/ProfissionalSaudeList';
import ProfissionalSaudeForm from './components/ProfissionalSaudeForm';

function App() {
  return (
    <Router>
      <div className="App">
        <nav className="navbar">
          <h1>📅 Atendimentos Web</h1>
          <div className="nav-links">
            <Link to="/contatos">Contatos</Link>
            <Link to="/compromissos">Compromissos</Link>
            <Link to="/atendimentos">Atendimentos</Link>
            <Link to="/profissionais">Profissionais</Link>
          </div>
        </nav>
        <main className="container">
          <Routes>
            <Route path="/" element={<ContatoList />} />
            <Route path="/contatos" element={<ContatoList />} />
            <Route path="/contatos/novo" element={<ContatoForm />} />
            <Route path="/contatos/editar/:id" element={<ContatoForm />} />
            <Route path="/compromissos" element={<CompromissoList />} />
            <Route path="/compromissos/novo" element={<CompromissoForm />} />
            <Route path="/compromissos/editar/:id" element={<CompromissoForm />} />
            <Route path="/exames" element={<ExameList />} />
            <Route path="/exames/novo" element={<ExameForm />} />
            <Route path="/exames/editar/:id" element={<ExameForm />} />
            <Route path="/atendimentos" element={<AtendimentoList />} />
            <Route path="/atendimentos/novo" element={<AtendimentoForm />} />
            <Route path="/atendimentos/editar/:id" element={<AtendimentoForm />} />
            <Route path="/profissionais" element={<ProfissionalSaudeList />} />
            <Route path="/profissionais/novo" element={<ProfissionalSaudeForm />} />
            <Route path="/profissionais/editar/:id" element={<ProfissionalSaudeForm />} />
          </Routes>
        </main>
      </div>
    </Router>
  );
}

export default App;
