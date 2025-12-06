package com.jhanpier.filmbox.ui;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jhanpier.filmbox.model.Profile;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ProfileManager {

    private static final String KEY_PROFILES = "profile_list";
    private static final String KEY_SELECTED_PROFILE = "selected_profile_id";

    // --------------------------------------------------------------------
    // Obtener nombre del archivo según el usuario actual
    // --------------------------------------------------------------------
    private static String getPrefsName(Context context) {
        SharedPreferences loginPrefs = context.getSharedPreferences("login_data", Context.MODE_PRIVATE);
        String email = loginPrefs.getString("email", "unknown");

        return "profiles_" + email;   // 🔥 SEPARA LOS PERFILES POR USUARIO
    }

    // --------------------------------------------------------------------
    // Obtener lista de perfiles
    // --------------------------------------------------------------------
    public static List<Profile> getProfiles(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(getPrefsName(context), Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_PROFILES, "");

        if (json == null || json.isEmpty()) return new ArrayList<>();

        Type type = new TypeToken<List<Profile>>() {}.getType();
        List<Profile> list = new Gson().fromJson(json, type);

        return (list != null) ? list : new ArrayList<>();
    }

    // --------------------------------------------------------------------
    // Guardar lista completa
    // --------------------------------------------------------------------
    public static void saveProfiles(Context context, List<Profile> profiles) {
        SharedPreferences prefs = context.getSharedPreferences(getPrefsName(context), Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_PROFILES, new Gson().toJson(profiles)).apply();
    }

    // --------------------------------------------------------------------
    // Crear nuevo perfil
    // --------------------------------------------------------------------
    public static void addProfile(Context context, Profile profile) {
        List<Profile> profiles = getProfiles(context);

        int nextId = 1;
        for (Profile p : profiles) {
            nextId = Math.max(nextId, p.getId() + 1);
        }

        profile.setId(nextId);
        profiles.add(profile);

        saveProfiles(context, profiles);
    }

    // --------------------------------------------------------------------
    // Actualizar perfil (solo nombre y avatar)
    // --------------------------------------------------------------------
    public static void updateProfile(Context context, Profile updatedProfile) {
        List<Profile> profiles = getProfiles(context);

        for (int i = 0; i < profiles.size(); i++) {
            if (profiles.get(i).getId() == updatedProfile.getId()) {

                Profile p = profiles.get(i);
                p.setName(updatedProfile.getName());
                p.setAvatarResId(updatedProfile.getAvatarResId());

                profiles.set(i, p);
                saveProfiles(context, profiles);
                return;
            }
        }
    }

    // --------------------------------------------------------------------
    // Buscar perfil por ID
    // --------------------------------------------------------------------
    public static Profile getProfileById(Context context, int id) {
        for (Profile p : getProfiles(context)) {
            if (p.getId() == id) return p;
        }
        return null;
    }

    // --------------------------------------------------------------------
    // Eliminar perfil
    // --------------------------------------------------------------------
    public static void deleteProfile(Context context, int id) {
        List<Profile> profiles = getProfiles(context);

        Iterator<Profile> iterator = profiles.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getId() == id) {
                iterator.remove();
                break;
            }
        }

        saveProfiles(context, profiles);
    }

    // --------------------------------------------------------------------
    // Guardar perfil seleccionado (es único por usuario)
    // --------------------------------------------------------------------
    public static void setSelectedProfile(Context context, int id) {
        SharedPreferences prefs = context.getSharedPreferences(getPrefsName(context), Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_SELECTED_PROFILE, id).apply();
    }

    // --------------------------------------------------------------------
    // Obtener perfil seleccionado
    // --------------------------------------------------------------------
    public static int getSelectedProfile(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(getPrefsName(context), Context.MODE_PRIVATE);
        return prefs.getInt(KEY_SELECTED_PROFILE, -1);
    }
}
