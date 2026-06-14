import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { examesService } from '../services/api';

function ExameList() {
  const [exames, setExames] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    carregarExames();
  }, []);

  const carregarExames = async () => {
    try {
      const response = await examesService.listar();
      setExames(response.data);
    } catch (error) {
      console.error('Erro ao carregar exames:', error);
    } finally {
      setLoading(false);
    }
  };

  const deletarExames = async (id) => {
    if (window.confirm('Tem certeza que deseja excluir este exames?')) {
      try {
        await examesService.deletar(id);
        carregarExames();
      } catch (error) {
        console.error('Erro ao deletar exame:', error);
      }
    }
  };

  if (loading) return <p>Carregando...</p>;

  return (
    <div>
      <div className="header">
        <h2>📋 Exames</h2>
        <Link to="/exames/novo" className="btn btn-primary">+ Novo Exame</Link>
      </div>

      <table className="table">
        <thead>
          <tr>
            <th>Descricao</th>
            <th>Posologia</th>
            <th>Acoes</th>
          </tr>
        </thead>
        <tbody>
          {exames.map(exame => (
            <tr key={exame.id}>
              <td>{exame.descricao}</td>
              <td>{exame.posologia}</td>
              <td>
                <Link to={`/exames/editar/${exame.id}`} className="btn btn-sm">Editar</Link>
                <button onClick={() => deletarExames(exame.id)} className="btn btn-danger btn-sm">
                  Excluir
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {exames.length === 0 && <p className="empty">Nenhum exame cadastrado.</p>}
    </div>
  );
}

export default ExameList;
