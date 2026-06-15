import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { atendimentoService, profissionalSaudeService } from '../services/api';

function AtendimentoForm() {
  const navigate = useNavigate();
  const { id } = useParams();
  const [atendimento, setAtendimento] = useState({
    titulo: '', data: '', horario: '', link_call: '', profissionalSaude: null, receitas: [], exames: []
  });
  const [profissionais, setProfissionais] = useState([]);
  const [novaReceita, setNovaReceita] = useState('');
  const [novoExame, setNovoExame] = useState({ descricao: '', posologia: '' });
  useEffect(() => {
    if (id) {
      atendimentoService.buscar(id)
        .then(response => {
          setAtendimento({ ...response.data, receitas: response.data.receitas || [], exames: response.data.exames || [] });
        })
        .catch(error => {
          console.error(error);
        });
    }
  }, [id]);
  useEffect(() => {
    profissionalSaudeService
      .listar()
      .then(res => setProfissionais(res.data))
      .catch(console.error);
  }, []);
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
          <label>Link da Reunião</label>
          <input
            type="text"
            value={
              atendimento.link_call
                ? atendimento.link_call.split('/').pop()
                : ''
            }
            readOnly
            placeholder="Nenhum link gerado"
          />
          {atendimento.link_call && (
            <a
              href={atendimento.link_call}
              target="_blank"
              rel="noopener noreferrer"
              style={{
                display: 'block',
                marginTop: '5px'
              }}
            >
              Entrar na reunião
            </a>
          )}
          <button
            type="button"
            className="btn btn-primary btn-sm"
            style={{
              marginTop: '8px',
              width: 'fit-content'
            }}
            onClick={async () => {
              try {
                const response = await atendimentoService.gerarLink();
                setAtendimento(prev => ({
                  ...prev,
                  link_call: response.data.link
                }));
              } catch (err) {
                console.error(err);
              }
            }}
          >
            {atendimento.link_call
              ? 'Gerar Novo Link'
              : 'Gerar Link'}
          </button>
        </div>
        <div className="form-group">
          <label>Profissional</label>
          <select
            value={atendimento.profissionalSaude?.id || ''}
            onChange={e =>
              setAtendimento({
                ...atendimento,
                profissionalSaude: {
                  id: parseInt(e.target.value)
                }
              })
            }
          >
            <option value="">Selecione</option>
            {profissionais.map(p => (
              <option key={p.id} value={p.id}>
                {p.nome}
              </option>
            ))}
          </select>
        </div>
        <div className="form-group">
          <label>Descrição do Exame</label>
          <input
            type="text"
            value={novoExame.descricao}
            onChange={e =>
              setNovoExame({
                ...novoExame,
                descricao: e.target.value
              })
            }
          />
        </div>

        <div className="form-group">
          <label>Posologia</label>
          <textarea
            value={novoExame.posologia}
            onChange={e =>
              setNovoExame({
                ...novoExame,
                posologia: e.target.value
              })
            }
          />
        </div>

        <div className="form-group">
          <button
            type="button"
            className="btn btn-primary"
            onClick={() => {
              if (!novoExame.descricao.trim()) return;

              setAtendimento(prev => ({
                ...prev,
                exames: [
                  ...prev.exames,
                  {
                    descricao: novoExame.descricao,
                    posologia: novoExame.posologia
                  }
                ]
              }));

              setNovoExame({
                descricao: '',
                posologia: ''
              });
            }}
          >
            Adicionar Exame
          </button>
        </div>

        <ul className="receitas-lista">
          {atendimento.exames.map((exame, index) => (
            <li className="receita-item" key={index}>
              <div>
                <strong>{exame.descricao}</strong>
                <br />
                {exame.posologia}
              </div>

              <button
                type="button"
                className="btn btn-danger"
                onClick={() =>
                  setAtendimento(prev => ({
                    ...prev,
                    exames: prev.exames.filter((_, i) => i !== index)
                  }))
                }
              >
                Remover
              </button>
            </li>
          ))}
        </ul>
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
