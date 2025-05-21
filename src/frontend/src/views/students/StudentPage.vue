<template>
  <div>
    <v-dialog v-model="dialog" width="90%">
      <template v-slot:activator="{ props: activatorProps }">
        <v-icon v-bind="activatorProps">mdi-dots-vertical</v-icon>
      </template>

      <v-card prepend-icon="mdi-account" title="Pagina do Estudante" class="d-flex flex-column position-relative pa-6"
        style="height: 90vh; overflow-y: auto;">
        <v-card-text>
          <v-container>

            <v-row>
              <!-- Right Side: Info fields -->
              <v-col cols="12" md="8">
                <v-text-field readonly label="Nome*" required v-model="newPerson.name"></v-text-field>
                <v-text-field readonly label="IST ID*" required v-model="newPerson.istId"></v-text-field>
                <img src="../assets/LOGO.png" style="position: absolute; padding-left: 50%; height: 160px;  right: 10%;top: 15%; z-index: 1;"/>
                <v-text-field readonly label="Email*" required v-model="newPerson.email"></v-text-field>
              </v-col>
            </v-row>

            <!-- Description Area -->
            <v-row>
              <v-col cols="12">
                <v-textarea label="Descrição" v-model="newPerson.description" rows="4"
                  :readonly="role.currentRole !== 'student'" auto-grow></v-textarea>
              </v-col>
            </v-row>

            <template
              v-if="role.currentRole === 'coordinator' && newPerson.workflowTese === 'STAFF' && !newPerson.workflowDef || newPerson.workflowDef === 'REVISION'">
              <v-row>
                <v-col cols="12" md="6">
                  <v-text-field v-model="newPerson.defenseDate" label="Data" type="date" />
                </v-col>
              </v-row>
            </template>

            <template v-if="newPerson.workflowDef && newPerson.workflowDef !== 'SUBMITED'">
              <h3>Defense Date:</h3>
              <v-row>
                <v-col cols="12" md="6">
                  <v-text-field readonly v-model="newPerson.defenseDate" label="Data" type="date" />
                </v-col>
              </v-row>
            </template>

            <template v-if="role.currentRole === 'admin' && newPerson.workflowTese === 'SUBMITED'">
              <v-row>
                <v-col cols="12">
                  <div class="text-h6 mb-4">Aprovar proposta?</div>
                  <v-btn color="success" @click="approve">Aprovar</v-btn>
                  <v-btn color="error" class="ml-4">Rejeitar</v-btn>
                </v-col>
              </v-row>
            </template>

            <template v-if="role.currentRole === 'coordinator' && newPerson.workflowTese === 'ATRIBUTED'">
              <v-row>
                <v-col cols="12">
                  <div class="text-h6 mb-4">Fazer upload do documento assinado</div>
                </v-col>

                <v-col cols="8">
                  <v-file-input label="Upload file" accept=".pdf,.docx,.png,.jpg" show-size dense
                    outlined />
                </v-col>

                <v-col cols="4" class="d-flex align-center">
                  <v-btn color="primary" @click="uploadSignedDocument">Submeter Documento Assinado</v-btn>
                </v-col>
              </v-row>
            </template>

            <template v-if="role.currentRole === 'coordinator' && newPerson.workflowDef === 'SCHEDULED'">
              <v-row>
                <v-col cols="12">
                  <div class="text-h6 mb-4">Nota da Defesa</div>
                  <v-row class="align-center">
                    <v-col cols="auto">
                      <v-text-field v-model.number="grade" label="Nota*" type="number" variant="outlined"
                        density="comfortable" :rules="[
                          v => v !== null && v !== '' || 'Campo obrigatório',
                          v => v >= 0 || 'Nota mínima é 0',
                          v => v <= 20 || 'Nota máxima é 20'
                        ]" required style="width: 150px;" />
                    </v-col>
                    <v-col cols="auto">
                      <v-btn color="primary" :disabled="!isGradeValid" class="ma-0" height="56" @click="uploadDocument">
                        Submeter Nota
                      </v-btn>
                    </v-col>
                  </v-row>
                </v-col>
              </v-row>
            </template>



            <template v-if="role.currentRole === 'staff' && newPerson.workflowTese === 'SIGNED'">
              <v-row>
                <v-col cols="12">
                  <div class="text-h6 mb-4">Submeter ao Fenix</div>
                  <v-btn color="primary" @click="submitToFenix">Submeter ao Fenix</v-btn>
                </v-col>
              </v-row>
            </template>
            <template v-if="role.currentRole === 'student' && !newPerson.workflowTese">
              <v-select label="Escolha os professores para o júri" :items="people" v-model="newPerson.teachers"
                item-title="name" item-value="id" multiple
                :rules="[v => v.length >= 1 && v.length <= 5 || 'Escolha entre 1 e 5 professores']" />
            </template>

            <!-- Actions Based on Role -->
            <template v-if="role.currentRole === 'coordinator' && newPerson.workflowTese === 'APPROVED'">
              <v-row>
                <v-col cols="12">
                  <div class="text-h6 mb-4">Escolher Presidente do Júri</div>
                </v-col>
              </v-row>
            </template>



            <!-- Teachers Selection Table -->
            <v-row>
              <v-col cols="12">
                <p><h2>Professores do juri</h2></p>
                <hr></hr>
                <v-data-table :headers="teacherTableHeaders" :items="selectedTeacherObjects" class="elevation-1"
                  density="compact">
                  <template #item.name="{ item }">
                    {{ item.name }}
                  </template>
                  <template #item.email="{ item }">
                    {{ item.email }}
                  </template>
                  <template #item.istId="{ item }">
                    {{ item.istId }}
                  </template>
                  <template v-slot:[`item.actions`]="{ item }">
                    <div class="d-flex">
                      <template v-if="role.currentRole === 'coordinator' && newPerson.workflowTese === 'APPROVED'">
                        <v-btn color="primary" @click="assignPresident()">Atribuir Presidente</v-btn>
                      </template>
                    </div>
                  </template>
                </v-data-table>
              </v-col>
            </v-row>

          </v-container>
        </v-card-text>

        <v-divider></v-divider>

        <v-card-actions>
          <v-spacer></v-spacer>

          <v-btn text="Close" variant="plain" @click="dialog = false"></v-btn>

          <v-btn color="primary" text="Save" variant="tonal" @click="dialog = false, submited(), editPerson()">
            Save
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import type PersonDto from '@/models/people/PersonDto'
import RemoteService from '@/services/RemoteService'
import { useRoleStore } from '@/stores/role'

const role = useRoleStore()

const dialog = ref(false)
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
  type: '',
  description: '',
  workflowTese: '',
  workflowDef: '',
  teachers: [],
  defenseDate: '',
  grade: ''
})

const people = ref<any[]>([])

const teacherTableHeaders = [
  { title: 'Nome', key: 'name', value: 'name', sortable: true, filterable: true },
  { title: 'IST ID', key: 'istId', value: 'istId', sortable: true, filterable: true },
  { title: 'Email', key: 'email', value: 'email', sortable: true, filterable: true },
  { title: 'Ações', key: 'actions' }
]

const selectedTeacherObjects = computed(() =>
  people.value.filter(p => newPerson.value.teachers.includes(p.id))
)

const init = () => {
  newPerson.value.id = prop.person.id
  newPerson.value.name = prop.person.name
  newPerson.value.istId = prop.person.istId
  newPerson.value.email = prop.person.email
  newPerson.value.type = getItemKey(typeMappings, prop.person.type)
  newPerson.value.description = prop.person.description
  newPerson.value.workflowTese = prop.person.workflowTese
  newPerson.value.teachers = prop.person.teachers
  newPerson.value.workflowDef = prop.person.workflowDef
  newPerson.value.defenseDate = prop.person.defenseDate
}

function getItemKey(rec: Record<string, string>, value: string): string {
  for (const [key, v] of Object.entries(rec)) {
    if (v === value) {
      return key
    }
  }
}


const getTeacher = async () => {
  try {
    const teachers = await RemoteService.getTeacher();
    if (Array.isArray(teachers)) {
      people.value = teachers;
    } else {
      console.error('Expected an array of teachers, but got:', teachers);
    }
  } catch (error) {
    console.error('Error fetching teachers:', error);
  }
}

const checkDate = () => {
  if (newPerson.value.defenseDate && !newPerson.value.workflowDef) {
    newPerson.value.workflowDef = 'SCHEDULED'
  }
}

const editPerson = async () => {
  checkDate()
  newPerson.value.type = typeMappings[newPerson.value.type as keyof typeof typeMappings]
  await RemoteService.editPerson(newPerson.value)
  newPerson.value = {
    name: '',
    istId: '',
    email: '',
    type: '',
    description: '',
    workflowTese: '',
    workflowDef: '',
    teachers: [],
    defenseDate: '',
    grade: '',
  }
  emit('person-edited')
}

function uploadDocument() {
  newPerson.value.grade = grade.value
  newPerson.value.workflowDef = 'SUBMITED'
  editPerson()
}

function approve() {
  newPerson.value.workflowTese = 'APPROVED'
  editPerson()
}

function uploadSignedDocument() {
  newPerson.value.workflowTese = 'SIGNED'
  editPerson()
}

function assignPresident() {

  newPerson.value.workflowTese = 'ATRIBUTED'
  editPerson()
}

function submitToFenix() {
  newPerson.value.workflowTese = 'STAFF'
  editPerson()
}

const submited = () => {
  if (newPerson.value.teachers.length !== 0 && !newPerson.value.workflowTese) {
    newPerson.value.workflowTese = 'SUBMITED'
  }
}

const grade = ref<number | null>(null)

const isGradeValid = computed(() => {
  return grade.value !== null && grade.value >= 0 && grade.value <= 20
})

onMounted(() => {
  init()
  getTeacher()
})
</script>
