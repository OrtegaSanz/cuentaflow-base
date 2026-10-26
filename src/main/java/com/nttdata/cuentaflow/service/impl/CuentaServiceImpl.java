package com.nttdata.cuentaflow.service.impl;

import com.nttdata.cuentaflow.dto.CuentaRequest;
import com.nttdata.cuentaflow.exception.CuentaDuplicadaException;
import com.nttdata.cuentaflow.exception.CuentaNoEncontradaException;
import com.nttdata.cuentaflow.exception.ReglaNegocioException;
import com.nttdata.cuentaflow.model.Cuenta;
import com.nttdata.cuentaflow.repository.CuentaRepository;
import com.nttdata.cuentaflow.service.CuentaService;
import com.nttdata.cuentaflow.util.GeneradorNumeroCuenta;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.math.BigDecimal;
import java.util.Comparator;
import org.springframework.stereotype.Service;

/**
 * Integracion: el repositorio devuelve tipos de Reactor (Mono / Flux) y aqui se convierten
 * a tipos de RxJava con fromPublisher (ambas librerias implementan Reactive Streams).
 */
@Service
public class CuentaServiceImpl implements CuentaService {

    private final CuentaRepository repository;

    public CuentaServiceImpl(CuentaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Flowable<Cuenta> listar() {
        // TODO 4: convierte el Flux<Cuenta> de repository.findAll() en un Flowable<Cuenta>.
        //  Pista: Flowable.fromPublisher(...) acepta cualquier Publisher de Reactive Streams.
        return Flowable.fromPublisher(repository.findAll());
    }

    @Override
    public Observable<Cuenta> listarPorCliente(String dni) {
        // TODO 5 (RN-07): convierte repository.findByDni(dni) en un Observable<Cuenta>
        //  y ordena las cuentas por numero.
        //  Pista: Observable.fromPublisher(...) + .sorted(Comparator.comparing(Cuenta::numero))
        return Observable.fromPublisher(repository.findByDni(dni)).sorted(Comparator.comparing(Cuenta::numero));
    }

    @Override
    public Single<Cuenta> consultar(String numero) {
        // TODO 6 (RN-06): busca con repository.findById(numero) (Mono: 0 o 1 cuenta).
        //  - Conviertelo a Maybe con Maybe.fromPublisher(...).
        //  - Si viene vacio, emite new CuentaNoEncontradaException(numero) usando switchIfEmpty + Single.error.
        return Maybe.fromPublisher(repository.findById(numero))
                .switchIfEmpty(Maybe.error(new CuentaNoEncontradaException(numero)))
                .toSingle();
    }

    @Override
    public Single<Cuenta> registrar(CuentaRequest request) {
        // TODO 8: registra la cuenta encadenando 3 pasos:
        //  1. Genera el numero con GeneradorNumeroCuenta.generar(tipo, dni, moneda).
        //  2. validarSaldoMinimo(request) y luego (andThen) verifica en MongoDB si el numero ya existe:
        //     Single.defer(() -> Single.fromPublisher(repository.existsById(numero)))
        //     (defer evita consultar MongoDB si el saldo minimo ya fallo).
        //  3. flatMap: si existe -> Single.error(new CuentaDuplicadaException(numero)) (RN-03);
        //     si no existe -> Single.fromPublisher(repository.save(Cuenta.abrir(numero, request))).
        String numero = GeneradorNumeroCuenta.generar(request.tipo(), request.dni(), request.moneda());

        return validarSaldoMinimo(request)
                .andThen(Single.defer(() -> Single.fromPublisher(repository.existsById(numero))))
                .flatMap(existe -> {
                    if (existe) {
                        return Single.error(new CuentaDuplicadaException(numero));
                    }
                    return Single.fromPublisher(repository.save(Cuenta.abrir(numero, request)));
                });
    }

    /** RN-04: el saldo inicial no puede ser menor al saldo minimo del tipo de cuenta. */
    Completable validarSaldoMinimo(CuentaRequest request) {
        // TODO 7 (RN-04): si request.saldoInicial() es menor que request.tipo().saldoMinimo(),
        //  devuelve Completable.error(new ReglaNegocioException("SALDO_MINIMO", "mensaje..."));
        //  en otro caso devuelve Completable.complete().
        //  Pista: compara BigDecimal con compareTo(...) < 0, nunca con <.
        if (request.saldoInicial().compareTo(request.tipo().saldoMinimo()) < 0) {
            return Completable.error(new ReglaNegocioException(
                    "SALDO_MINIMO",
                    "El saldo inicial no puede ser menor al mínimo requerido"
            ));
        }
        return Completable.complete();
    }
}
