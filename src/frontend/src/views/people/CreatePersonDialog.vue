<template>
  <div class="pa-4 text-center">
    <v-dialog v-model="dialog" max-width="400">
      <template v-slot:activator="{ props: activatorProps }">
        <v-btn class="text-none font-weight-regular" prepend-icon="mdi-plus" text="Adicionar Pessoa"
          v-bind="activatorProps" color="primary"></v-btn>
      </template>

      <v-card prepend-icon="mdi-account" title="Nova">
        <v-alert v-if="errorMessage" type="error" class="mb-4" dense border="start" border-color="red">
          {{ errorMessage }}
        </v-alert>
        <v-form ref="form" v-model="isFormValid">
          <v-card-text>
            <v-text-field label="Nome*" required v-model="newPerson.name" :rules ="[nameRule]"></v-text-field>
            <v-text-field label="IST ID*" required v-model="newPerson.istId" :rules="[istIdRule]"></v-text-field>
            <v-text-field label="Email*" required v-model="newPerson.email" :rules="[emailRule]"></v-text-field>


            <!-- Correct v-select binding to newPerson.type -->
            <v-select :items="['Coordenador', 'Staff', 'Aluno', 'Professor', 'SC']" label="Categoria*" required
              v-model="newPerson.type" :rules="[typeRequiredRule]">
            </v-select>
          </v-card-text>

          <v-divider></v-divider>

          <v-card-actions>
            <v-spacer></v-spacer>

            <v-btn text="Close" variant="plain" @click="dialog = false"></v-btn>

            <!-- Disable save button until the form is valid -->
            <v-btn color="primary" text="Save" variant="tonal" :disabled="!isFormValid"
              @click="onSave"></v-btn>
          </v-card-actions>
        </v-form>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import type PersonDto from '@/models/people/PersonDto'
import RemoteService from '@/services/RemoteService'

const dialog = ref(false)
const isFormValid = ref(false) // This will track if the form is valid
const emit = defineEmits(['person-created'])
const errorMessage = ref('')


const typeMappings = {
  Coordenador: 'COORDINATOR',
  Staff: 'STAFF',
  Aluno: 'STUDENT',
  Professor: 'TEACHER',
  SC: 'SC'
}

const workflowMappings = {
  Completo: 'COMPLETE',
  'Fase Final': 'FINAL_PHASE',
  Metade: 'MID',
  Inicio: 'BEGINNING'
}

const newPerson = ref<PersonDto>({
  name: '',
  email: '',
  istId: '',
  type: '' // Make sure this is empty initially, or set to a valid value from props if required.
})

// Validation rule for IST ID: must start with "ist1" followed by a digit.
const istIdRule = (value: string) => {
  const pattern = /^ist1\d*$/i
  const maxLength = 10
  return (
    (pattern.test(value) && value.length <= maxLength) ||
    `IstID tem de começar com 'ist1' e ser menor que ${maxLength} caracteres`
  );
}
// Validation rule for ensuring that type is selected
const typeRequiredRule = (value: string) => {
  return value && value.length > 0 || 'Tipo é obrigatório'
}

// Validation rule for email: must match a typical email pattern
const emailRule = (value: string) => {
  const pattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/
  return pattern.test(value) || 'Por-favor introduza um email valido'
}

const checkPersonExists = async (newPerson: PersonDto) => {
  const exists = await RemoteService.checkPersonExists(newPerson);
  return exists;
}

const nameRule = (value: string) => {
  const pattern = /^[^\d]+$/ // Disallows digits
  return pattern.test(value) || 'Nome não pode conter números'
}


const onSave = async () => {
  await savePerson()
  if (!errorMessage.value) {
    dialog.value= false
  }
}

const savePerson = async () => {
  const exists = await checkPersonExists(newPerson.value);

  if (exists) {
    errorMessage.value = 'Já existe uma pessoa com este IST ID, nome ou email.'
    return;
  }

  newPerson.value.type = typeMappings[newPerson.value.type as keyof typeof typeMappings]
  await RemoteService.createPerson(newPerson.value)

  newPerson.value = {
    name: '',
    email: '',
    istId: '',
    type: ''
  }

  emit('person-created')
}
</script>
