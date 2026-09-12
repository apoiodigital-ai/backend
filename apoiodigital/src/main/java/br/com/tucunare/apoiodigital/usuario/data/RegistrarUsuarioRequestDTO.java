package br.com.tucunare.apoiodigital.usuario.data;

/**
 * Body of {@code POST /usuario/registrar}, called once by the partner's app (via
 * {@code CaneSDK.registerUser()}) to create the backend-side identity that {@code userId} in
 * every other SDK endpoint refers to. {@code nome} is optional and, per the anonymization
 * requirement, should never be a real name tying back to a real-world identity.
 */
public record RegistrarUsuarioRequestDTO(String nome) {
}
