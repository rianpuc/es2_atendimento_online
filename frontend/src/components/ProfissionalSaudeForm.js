import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { profissionalSaudeService } from '../services/api';

function ProfissionalSaudeForm() {
    const navigate = useNavigate();
    const { id } = useParams();
    const [profissionalSaude, setProfissionalSaude] = useState({
        nome: '', telefone: '', endereco: '', categoria: ''
    });

    useEffect(() => {
        if (id) {
            profissionalSaudeService.buscar(id).then(res => setProfissionalSaude(res.data));
        }
    }, [id]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            if (id) {
                await profissionalSaudeService.atualizar(id, profissionalSaude);
            } else {
                await profissionalSaudeService.criar(profissionalSaude);
            }
            navigate('/profissionais');
        } catch (error) {
            console.error('Erro ao salvar profissional:', error);
        }
    };

    return (
        <div>
            <h2>{id ? 'Editar Profissional' : 'Novo Profissional'}</h2>
            <form onSubmit={handleSubmit} className="form">
                <div className="form-group">
                    <label>Nome *</label>
                    <input type="text" value={profissionalSaude.nome} required
                        onChange={e => setProfissionalSaude({ ...profissionalSaude, nome: e.target.value })} />
                </div>
                <div className="form-group">
                    <label>Telefone</label>
                    <input type="text" value={profissionalSaude.telefone}
                        onChange={e => setProfissionalSaude({ ...profissionalSaude, telefone: e.target.value })} />
                </div>
                <div className="form-group">
                    <label>Endereco</label>
                    <input type="text" value={profissionalSaude.endereco}
                        onChange={e => setProfissionalSaude({ ...profissionalSaude, endereco: e.target.value })} />
                </div>
                <div className="form-group">
                    <label>Categoria *</label>
                    <select
                        value={profissionalSaude.categoria}
                        onChange={e =>
                            setProfissionalSaude({
                                ...profissionalSaude,
                                categoria: e.target.value
                            })
                        }
                        required
                    >
                        <option value="">Selecione uma categoria</option>
                        <option value="MEDICO">Médico</option>
                        <option value="PSICOLOGO">Psicólogo</option>
                        <option value="FISIOTERAPEUTA">Fisioterapeuta</option>
                    </select>
                </div>
                <button type="submit" className="btn btn-primary">Salvar</button>
                <button type="button" className="btn" onClick={() => navigate('/profissionais')}>Cancelar</button>
            </form>
        </div>
    );
}

export default ProfissionalSaudeForm;