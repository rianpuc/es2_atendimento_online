import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { atendimentoService } from '../services/api';

function AtendimentoList() {
  const [atendimentos, setAtendimentos] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    carregarAtendimentos();
  }, []);

  const carregarAtendimentos = async () => {
    try {
      const response = await atendimentoService.listar();
      setAtendimentos(response.data);
    } catch (error) {
      console.error('Erro ao carregar atendimentos:', error);
    } finally {
      setLoading(false);
    }
  };

  const deletarAtendimentos = async (id) => {
    if (window.confirm('Tem certeza que deseja excluir este atendimento?')) {
      try {
        await atendimentoService.deletar(id);
        carregarAtendimentos();
      } catch (error) {
        console.error('Erro ao deletar atendimento:', error);
      }
    }
  };

  if (loading) return <p>Carregando...</p>;

  return (
    <div>
      <div className="header">
        <h2>📅 Atendimentos</h2>
        <Link to="/atendimentos/novo" className="btn btn-primary">+ Novo Atendimento</Link>
      </div>

      <table className="table">
        <thead>
          <tr>
            <th>Título</th>
            <th>Data</th>
            <th>Hora</th>
            <th>Link</th>
            <th>Receitas</th>
            <th>Acoes</th>
          </tr>
        </thead>
        <tbody>
          {atendimentos.map(atend => (
            <tr key={atend.id}>
              <td>{atend.titulo}</td>
              <td>{atend.data}</td>
              <td>{atend.horario}</td>
              <td>{atend.link_call}</td>
              <td>{(atend.receitas || []).join(', ')}</td>
              <td>
                <Link to={`/atendimentos/editar/${atend.id}`} className="btn btn-sm">Editar</Link>
                <button onClick={() => deletarAtendimentos(atend.id)} className="btn btn-danger btn-sm">
                  Excluir
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {atendimentos.length === 0 && <p className="empty">Nenhum atendimento cadastrado.</p>}
    </div>
  );
}

export default AtendimentoList;
