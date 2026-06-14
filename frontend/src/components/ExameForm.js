import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { examesService } from '../services/api';

function ExameForm() {
  const navigate = useNavigate();
  const { id } = useParams();
  const [exame, setExame] = useState({
    descricao: '', posologia: ''
  });

  useEffect(() => {
    if (id) {
      examesService.buscar(id).then(res => setExame(res.data));
    }
  }, [id]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (id) {
        await examesService.atualizar(id, exame);
      } else {
        await examesService.criar(exame);
      }
      navigate('/exames');
    } catch (error) {
      console.error('Erro ao salvar exame:', error);
    }
  };

  return (
    <div>
      <h2>{id ? 'Editar Exame' : 'Novo Exame'}</h2>
      <form onSubmit={handleSubmit} className="form">
        <div className="form-group">
          <label>Descricao*</label>
          <input type="text" value={exame.descricao} required
            onChange={e => setExame({ ...exame, descricao: e.target.value })} />
        </div>
        <div className="form-group">
          <label>Posologia*</label>
          <input type="text" value={exame.posologia}
            onChange={e => setExame({ ...exame, posologia: e.target.value })} />
        </div>
        <button type="submit" className="btn btn-primary">Salvar</button>
        <button type="button" className="btn" onClick={() => navigate('/exames')}>Cancelar</button>
      </form>
    </div>
  );
}

export default ExameForm;
