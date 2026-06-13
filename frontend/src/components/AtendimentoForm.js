import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { atendimentoService } from '../services/api';

function AtendimentoForm() {
  const navigate = useNavigate();
  const { id } = useParams();
  const [atendimento, setAtendimento] = useState({
    titulo: '', data: '', horario: '', link_call: '', receitas: []
  });
  const [novaReceita, setNovaReceita] = useState('');
  useEffect(() => {
    if (id) {
      atendimentoService.buscar(id)
        .then(response => {
          setAtendimento(response.data);
        })
        .catch(error => {
          console.error(error);
        });
    }
  }, [id]);
  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (id) {
        await atendimentoService.atualizar(id, atendimento);
      } else {
        await atendimentoService.criar(atendimento);
      }
      navigate('/atendimentos');
    } catch (error) {
      console.error('Erro ao salvar atendimento:', error);
    }
  };

  return (
    <div>
      <h2>{id ? 'Editar Atendimento' : 'Novo Atendimento'}</h2>
      <form onSubmit={handleSubmit} className="form">
        <div className="form-group">
          <label>Título *</label>
          <input type="text" value={atendimento.titulo} required
            onChange={e => setAtendimento({ ...atendimento, titulo: e.target.value })} />
        </div>
        <div className="form-group">
          <label>Data *</label>
          <input type="date" value={atendimento.data} required
            onChange={e => setAtendimento({ ...atendimento, data: e.target.value })} />
        </div>
        <div className="form-group">
          <label>Horario</label>
          <input type="time" value={atendimento.horario}
            onChange={e => setAtendimento({ ...atendimento, horario: e.target.value })} />
        </div>
        <div className="form-group">
          <label>Link Call</label>
          <textarea value={atendimento.link_call}
            onChange={e => setAtendimento({ ...atendimento, link_call: e.target.value })} />
        </div>
        <div className="form-group">
          <label>Receitas</label>
          <div style={{ display: 'flex', gap: '10px' }}>
            <input
              type="text"
              placeholder="Digite uma receita"
              value={novaReceita}
              onChange={(e) => setNovaReceita(e.target.value)}
            />
            <button
              className="btn btn-primary"
              type="button"
              onClick={() => {
                if (!novaReceita.trim()) return;
                setAtendimento(prev => ({
                  ...prev,
                  receitas: [...prev.receitas, novaReceita]
                }));
                setNovaReceita('');
              }}
            >
              Adicionar
            </button>
          </div>
          <ul className="receitas-lista">
            {atendimento.receitas.map((receita, index) => (
              <li className="receita-item" key={index}>
                {receita}
                <button
                  className="btn btn-danger"
                  type="button"
                  onClick={() =>
                    setAtendimento(prev => ({
                      ...prev,
                      receitas: prev.receitas.filter((_, i) => i !== index)
                    }))
                  }
                >
                  Remover
                </button>
              </li>
            ))}
          </ul>
        </div>
        <button type="submit" className="btn btn-primary">Salvar</button>
        <button type="button" className="btn" onClick={() => navigate('/atendimentos')}>Cancelar</button>
      </form>
    </div>
  );
}

export default AtendimentoForm;
