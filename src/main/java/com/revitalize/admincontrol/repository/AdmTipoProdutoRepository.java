package com.revitalize.admincontrol.repository;

import com.revitalize.admincontrol.models.AdmTipoProdutoModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AdmTipoProdutoRepository extends CrudRepository<AdmTipoProdutoModel, UUID> {


}