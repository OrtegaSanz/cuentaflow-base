package com.nttdata.cuentaflow.controller;

import com.nttdata.cuentaflow.dto.CuentaRequest;
import com.nttdata.cuentaflow.model.Cuenta;
import com.nttdata.cuentaflow.service.CuentaService;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * La capa web usa Spring WebFlux (Reactor): convierte los tipos RxJava del servicio a Mono / Flux.
 * Regla: el controller nunca llama a subscribe() ni a block(); WebFlux se suscribe por nosotros.
 */
@RestController
@RequestMapping("/api/v1")
public class CuentaController {

    private final CuentaService service;

    public CuentaController(CuentaService service) {
        this.service = service;
    }

    /** Ejemplo resuelto: GET /api/v1/cuentas  ->  Flowable (RxJava) a Flux (Reactor). */
    @GetMapping("/cuentas")
    public Flux<Cuenta> listar() {
        return Flux.from(service.listar());
    }

    // TODO 9: crea el endpoint GET /api/v1/clientes/{dni}/cuentas que devuelva Flux<Cuenta>.
    //  - Delega en service.listarPorCliente(dni) (devuelve Observable).
    //  - Observable NO es un Publisher: conviertelo con toFlowable(BackpressureStrategy.BUFFER)
    //    y luego Flux.from(...).
    @GetMapping("/clientes/{dni}/cuentas")
    public Flux<Cuenta> listarCuentasXdni(@PathVariable String dni){
        return Flux.from(service.listarPorCliente(dni).toFlowable(BackpressureStrategy.BUFFER));
    }


    // TODO 10: crea el endpoint GET /api/v1/cuentas/{numero} que devuelva Mono<Cuenta>.
    //  - Delega en service.consultar(numero) (devuelve Single).
    //  - Conviertelo con Mono.from(single.toFlowable()).
    @GetMapping("/cuentas/{numero}")
    public Mono<Cuenta> cuentaXnumero(@PathVariable String numero){
        return Mono.from(service.consultar(numero).toFlowable());
    }

    // TODO 11: crea el endpoint POST /api/v1/cuentas que devuelva Mono<Cuenta> con estado 201.
    //  - Usa @PostMapping, @ResponseStatus(HttpStatus.CREATED) y @Valid @RequestBody CuentaRequest request.
    //  - Delega en service.registrar(request) y conviertelo a Mono igual que en el TODO 10.
    @PostMapping("/cuentas")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Cuenta> registrarCuenta(@Valid @RequestBody CuentaRequest request){
        return Mono.from(service.registrar(request).toFlowable());
    }
}
