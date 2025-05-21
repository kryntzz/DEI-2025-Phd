<template>
  <div>
    <v-dialog v-model="dialog" max-width="400">
      <template v-slot:activator="{ props: activatorProps }">
        <v-icon v-bind="activatorProps">mdi-pencil</v-icon>
      </template>

      <v-card prepend-icon="mdi-account" title="Editar">
        <v-card-text>
          <v-form ref="form" v-model="isFormValid">
            <!-- Name Field (No Validation in your original code) -->
            <v-text-field label="Nome*" required v-model="newPerson.name"></v-text-field>

            <!-- IST ID Field with Validation -->
            <v-text-field
              label="IST ID*"
              required
              v-model="newPerson.istId"
              :rules="[istIdRule]"
            ></v-text-field>

            <!-- Email Field with Validation -->
            <v-text-field
              label="Email*"
              required
              v-model="newPerson.email"
              :rules="[emailRule]"
            ></v-text-field>

            <!-- Category Field with Validation -->
            <v-select
              :items="['Coordenador', 'Staff', 'Aluno', 'Professor', 'SC']"
              label="Categoria*"
              required
              v-model="newPerson.type"
              :rules="[typeRequiredRule]"
            ></v-select>
          </v-form>
        </v-card-text>

        <v-divider></v-divider>

        <v-card-actions>
          <v-spacer></v-spacer>

          <v-btn text="Close" variant="plain" @click="dialog = false"></v-btn>

          <!-- Save Button with Disabled State If Form is Invalid -->
          <v-btn
            color="primary"
            text="Save"
            variant="tonal"
            :disabled="!isFormValid"
            @click="dialog = false; editPerson()"
          ></v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import type PersonDto from '@/models/people/PersonDto'
import RemoteService from '@/services/RemoteService'

const dialog = ref(false)
const isFormValid = ref(false)  // Track if the form is valid

const emit = defineEmits(['person-edited'])
const prop = defineProps({ person: Object })

const typeMappings = {
  Coordenador: 'COORDINATOR',
  Staff: 'STAFF',
  Aluno: 'STUDENT',
  Professor: 'TEACHER',
  SC: 'SC'
}

const newPerson = ref<PersonDto>({
  name: '',
  istId: '',
  email: '',
  type: ''
})

// Validation rule for IST ID: must start with "ist1" followed by a digit
const istIdRule = (value: string) => {
  const pattern = /^ist1[0-9]{1,}$/i
  return pattern.test(value) || 'IST ID must start with "ist1" followed by a number'
}

// Validation rule for Category: must be selected
const typeRequiredRule = (value: string) => {
  return value && value.length > 0 || 'Categoria é obrigatória'
}

// Validation rule for Email: must match a valid email format
const emailRule = (value: string) => {
  const pattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/
  return pattern.test(value) || 'Please enter a valid email address'
}

// Get the key for a type mapping (for reverse lookup)
function getItemKey(rec: Record<string, string>, value: string): string {
  for (const [key, v] of Object.entries(rec)) {
    if (v === value) {
      return key
    }
  }
}

// Initialize the form with the prop person data
const init = () => {
  newPerson.value.id = prop.person.id
  newPerson.value.name = prop.person.name
  newPerson.value.istId = prop.person.istId
  newPerson.value.email = prop.person.email
  newPerson.value.type = getItemKey(typeMappings, prop.person.type)
}

// Save the edited person
const editPerson = async () => {
  newPerson.value.type = typeMappings[newPerson.value.type as keyof typeof typeMappings]
  await RemoteService.editPerson(newPerson.value)
  
  // Reset form after submission
  newPerson.value = {
    name: '',
    istId: '',
    email: '',
    type: ''
  }

  // Emit the 'person-edited' event
  emit('person-edited')
}

init()
</script>
