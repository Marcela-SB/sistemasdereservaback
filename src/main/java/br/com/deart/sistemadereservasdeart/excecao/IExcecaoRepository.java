package br.com.deart.sistemadereservasdeart.excecao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IExcecaoRepository extends JpaRepository<ExcecaoModel, UUID> {

}
