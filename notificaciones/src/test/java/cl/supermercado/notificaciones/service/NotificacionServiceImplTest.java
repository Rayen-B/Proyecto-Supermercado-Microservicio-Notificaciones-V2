package cl.supermercado.notificaciones.service;

import cl.supermercado.notificaciones.dto.remote.LoginDto;
import cl.supermercado.notificaciones.dto.remote.RolDto;
import cl.supermercado.notificaciones.dto.request.NotificacionRequestDto;
import cl.supermercado.notificaciones.dto.response.NotificacionResponseDto;
import cl.supermercado.notificaciones.mapper.NotificacionMapper;
import cl.supermercado.notificaciones.model.Notificacion;
import cl.supermercado.notificaciones.repository.NotificacionRepository;
import cl.supermercado.notificaciones.service.api.UsuariosClient;
import cl.supermercado.notificaciones.service.impl.NotificacionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios - NotificacionServiceImpl")
public class NotificacionServiceImplTest {

    @Mock
    private NotificacionRepository repository;

    @Mock
    private NotificacionMapper mapper;          // ← AGREGAR

    @Mock
    private UsuariosClient usuariosClient;      // ← AGREGAR

    @InjectMocks
    private NotificacionServiceImpl service;

    private Notificacion notificacion;
    private NotificacionRequestDto requestDto;
    private NotificacionResponseDto responseDto;

    @BeforeEach
    void setUp() {
        notificacion = new Notificacion();
        requestDto = new NotificacionRequestDto(1L, "Mensaje de prueba"); // ← datos reales
        responseDto = new NotificacionResponseDto();
    }

    @Test
    @DisplayName("enviarNotificacion: debe guardar la notificación correctamente")
    void enviarNotificacion_DeberiaGuardar_CuandoSeEnviaRequest() {
        when(mapper.toEntity(requestDto)).thenReturn(notificacion);      // ← AGREGAR
        when(repository.save(any(Notificacion.class))).thenReturn(notificacion);
        when(mapper.toDto(notificacion)).thenReturn(responseDto);         // ← AGREGAR

        NotificacionResponseDto resultado = service.enviarNotificacion(requestDto);

        assertThat(resultado).isNotNull();
        verify(repository, times(1)).save(any(Notificacion.class));
    }

    @Test
    @DisplayName("enviarNotificacion con UserId: debe validar FUNCIONARIO y guardar")
    void enviarNotificacion_DeberiaGuardar_CuandoSePasaUserIdYRequest() {
        // ← AGREGAR mock del cliente de usuarios
        RolDto rol = new RolDto(1L, "FUNCIONARIO");
        LoginDto login = new LoginDto(1L, "funcionario@test.cl", rol);
        when(usuariosClient.getRol(1L)).thenReturn(login);

        when(mapper.toEntity(requestDto)).thenReturn(notificacion);
        when(repository.save(any(Notificacion.class))).thenReturn(notificacion);
        when(mapper.toDto(notificacion)).thenReturn(responseDto);

        NotificacionResponseDto resultado = service.enviarNotificacion(1L, requestDto);

        assertThat(resultado).isNotNull();
        verify(repository, times(1)).save(any(Notificacion.class));
    }

    @Test
    @DisplayName("marcarComoLeida: debe procesar la notificación por su ID")
    void marcarComoLeida_DeberiaFuncionar_CuandoElIdExiste() {
        when(repository.findById(1L)).thenReturn(Optional.of(notificacion));
        when(repository.save(any(Notificacion.class))).thenReturn(notificacion);
        when(mapper.toDto(notificacion)).thenReturn(responseDto);         // ← AGREGAR

        NotificacionResponseDto resultado = service.marcarComoLeida(1L);

        assertThat(resultado).isNotNull();
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("listarPorUsuario: debe devolver la lista de notificaciones de un usuario")
    void listarPorUsuario_DeberiaDevolverLista() {
        // ← AGREGAR mocks reales
        when(repository.findByUsuarioId(1L)).thenReturn(List.of(notificacion));
        when(mapper.toDto(notificacion)).thenReturn(responseDto);

        List<NotificacionResponseDto> resultado = service.listarPorUsuario(1L);

        assertThat(resultado).isNotNull().hasSize(1);
        verify(repository, times(1)).findByUsuarioId(1L);
    }

    @Test
    @DisplayName("obtenerPorId: debe devolver la notificación cuando existe")  // ← TEST NUEVO
    void obtenerPorId_DeberiaDevolverNotificacion_CuandoExiste() {
        when(repository.findById(1L)).thenReturn(Optional.of(notificacion));
        when(mapper.toDto(notificacion)).thenReturn(responseDto);

        NotificacionResponseDto resultado = service.obtenerPorId(1L);

        assertThat(resultado).isNotNull();
        verify(repository, times(1)).findById(1L);
    }
}