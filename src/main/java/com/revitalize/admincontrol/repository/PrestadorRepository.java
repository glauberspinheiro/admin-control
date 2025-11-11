package com.revitalize.admincontrol.repository;

import com.revitalize.admincontrol.models.PrestadorModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PrestadorRepository extends JpaRepository<PrestadorModel, Long> {

    @Query(value = "SELECT p.*,\n" +
      " (6371 * acos(\n" +
      "   cos(radians(:lat)) * cos(radians(p.lat)) * cos(radians(p.lng) - radians(:lng)) +\n" +
      "   sin(radians(:lat)) * sin(radians(p.lat))\n" +
      " )) AS distance_km\n" +
      "FROM TB_PRESTADOR p\n" +
      "WHERE (:tipo IS NULL OR :tipo = '' OR p.tipo = :tipo)\n" +
      "  AND p.lat BETWEEN (:lat - :delta) AND (:lat + :delta)\n" +
      "  AND p.lng BETWEEN (:lng - :delta) AND (:lng + :delta)\n" +
      "ORDER BY distance_km ASC\n" +
      "LIMIT :limit", nativeQuery = true)
    List<PrestadorModel> findNearest(
        @Param("lat") double lat,
        @Param("lng") double lng,
        @Param("tipo") String tipo,
        @Param("delta") double delta,
        @Param("limit") int limit
    );
}
