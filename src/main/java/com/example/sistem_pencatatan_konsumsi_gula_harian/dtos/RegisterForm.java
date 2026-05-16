package com.example.sistem_pencatatan_konsumsi_gula_harian.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterForm {

    @NotBlank(message = "Nama wajib diisi")
    @Size(max = 100, message = "Nama maksimal 100 karakter")
    private String name;

    @NotBlank(message = "Username wajib diisi")
    @Size(min = 3, max = 50, message = "Username harus 3-50 karakter")
    private String username;

    @NotBlank(message = "Password wajib diisi")
    @Size(min = 6, max = 255, message = "Password minimal 6 karakter")
    private String password;

    @NotBlank(message = "Konfirmasi password wajib diisi")
    private String confirmPassword;

    public boolean isPasswordMatch() {
        return password != null && password.equals(confirmPassword);
    }
}
