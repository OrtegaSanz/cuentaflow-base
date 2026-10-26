package com.nttdata.cuentaflow.dto;

import com.nttdata.cuentaflow.model.Moneda;
import com.nttdata.cuentaflow.model.TipoCuenta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

/** Cuerpo del POST /api/v1/cuentas. Las anotaciones aplican la regla RN-01. */
public record CuentaRequest(
        // TODO 1 (RN-01): agrega Bean Validation a cada componente del record:
        //  - dni: obligatorio (@NotBlank) y exactamente 8 digitos (@Pattern con la expresion \\d{8})
        //  - titular: obligatorio (@NotBlank)
        //  - tipo y moneda: obligatorios (@NotNull)
        //  - saldoInicial: obligatorio (@NotNull) y no negativo (@PositiveOrZero)
        //  Usa message = "..." para que el error sea claro (ej. "debe tener 8 digitos")
        //  .
        @Pattern(regexp = "\\d{8}", message="debe tener 8 digitos")
        @NotBlank(message = "no puede estar vacío")
        String dni,
        @NotBlank(message = "es obligatorio")
        String titular,
        @NotNull(message = "es obligatorio")
        TipoCuenta tipo,
        @NotNull(message = "es obligatoiro")
        Moneda moneda,
        @NotNull(message = "es obligatoiro")
        @PositiveOrZero(message = "no puede ser negativo")
        BigDecimal saldoInicial) {
}
